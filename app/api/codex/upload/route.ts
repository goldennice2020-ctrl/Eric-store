import { NextResponse } from "next/server";
import { z } from "zod";
import { verifyUploadToken } from "@/lib/auth";
import { APP_STATUSES } from "@/lib/constants";
import { ensureDatabase } from "@/lib/db";
import { saveApk, saveIcon } from "@/lib/files";
import { prisma } from "@/lib/prisma";

export const runtime = "nodejs";
export const dynamic = "force-dynamic";

const uploadSchema = z.object({
  name: z.string().min(1),
  packageName: z.string().min(1),
  versionName: z.string().min(1),
  versionCode: z.coerce.number().int().optional(),
  description: z.string().optional().default(""),
  changelog: z.string().optional().default(""),
  status: z.enum(APP_STATUSES).optional().default("AVAILABLE")
});

function text(formData: FormData, key: string) {
  return String(formData.get(key) || "").trim();
}

function bearerToken(request: Request) {
  const auth = request.headers.get("authorization") || "";
  if (auth.toLowerCase().startsWith("bearer ")) {
    return auth.slice(7).trim();
  }
  return "";
}

export async function POST(request: Request) {
  const formData = await request.formData();
  const token = bearerToken(request) || text(formData, "token");

  if (!verifyUploadToken(token)) {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }

  await ensureDatabase();

  const apk = formData.get("apk") as File | null;
  if (!apk || apk.size === 0) {
    return NextResponse.json({ error: "Missing apk file" }, { status: 400 });
  }

  const parsed = uploadSchema.safeParse({
    name: text(formData, "name"),
    packageName: text(formData, "packageName"),
    versionName: text(formData, "versionName"),
    versionCode: text(formData, "versionCode") || undefined,
    description: text(formData, "description"),
    changelog: text(formData, "changelog"),
    status: text(formData, "status") || undefined
  });

  if (!parsed.success) {
    return NextResponse.json(
      { error: "Invalid metadata", issues: parsed.error.flatten() },
      { status: 400 }
    );
  }

  try {
    const input = parsed.data;
    const iconUrl = await saveIcon(formData.get("icon") as File | null);
    const saved = await saveApk(apk, input.packageName, input.versionName);

    const result = await prisma.$transaction(async (tx) => {
      const app = await tx.appProject.upsert({
        where: { packageName: input.packageName },
        create: {
          name: input.name,
          packageName: input.packageName,
          description: input.description,
          status: input.status,
          iconUrl
        },
        update: {
          name: input.name,
          description: input.description,
          status: input.status,
          updatedAt: new Date(),
          ...(iconUrl ? { iconUrl } : {})
        }
      });

      await tx.appRelease.updateMany({
        where: { appProjectId: app.id },
        data: { isLatest: false }
      });

      const release = await tx.appRelease.create({
        data: {
          appProjectId: app.id,
          versionName: input.versionName,
          versionCode: input.versionCode ?? null,
          apkUrl: saved.url,
          apkFileName: saved.fileName,
          apkSize: saved.size,
          sha256: saved.sha256,
          changelog: input.changelog,
          isLatest: true
        }
      });

      await tx.appProject.update({
        where: { id: app.id },
        data: {
          latestVersionId: release.id,
          updatedAt: new Date()
        }
      });

      return { app, release };
    });

    return NextResponse.json({
      ok: true,
      appId: result.app.id,
      releaseId: result.release.id,
      detailUrl: `/apps/${result.app.id}`,
      apkUrl: result.release.apkUrl,
      sha256: result.release.sha256
    });
  } catch (error) {
    return NextResponse.json(
      { error: error instanceof Error ? error.message : "Upload failed" },
      { status: 400 }
    );
  }
}
