"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { z } from "zod";
import { clearAdminSession, requireAuth, setAdminSession, verifyPassword } from "@/lib/auth";
import { APP_STATUSES } from "@/lib/constants";
import { ensureDatabase } from "@/lib/db";
import { saveApk, saveIcon, deleteApkFile } from "@/lib/files";
import { prisma } from "@/lib/prisma";

const appSchema = z.object({
  name: z.string().min(1),
  packageName: z.string().min(1),
  description: z.string().default(""),
  status: z.enum(APP_STATUSES)
});

function text(formData: FormData, key: string) {
  return String(formData.get(key) || "").trim();
}

function optionalInt(value: string) {
  if (!value) return null;
  const parsed = Number.parseInt(value, 10);
  return Number.isFinite(parsed) ? parsed : null;
}

export async function loginAction(formData: FormData) {
  const password = text(formData, "password");
  if (!verifyPassword(password)) {
    redirect("/admin/login?error=1");
  }
  await setAdminSession();
  redirect("/admin");
}

export async function logoutAction() {
  await clearAdminSession();
  redirect("/");
}

export async function createAppAction(formData: FormData) {
  await requireAuth();
  await ensureDatabase();
  const parsed = appSchema.parse({
    name: text(formData, "name"),
    packageName: text(formData, "packageName"),
    description: text(formData, "description"),
    status: text(formData, "status")
  });
  const iconUrl = await saveIcon(formData.get("icon") as File | null);
  const app = await prisma.appProject.create({
    data: {
      ...parsed,
      iconUrl
    }
  });
  revalidatePath("/");
  redirect(`/admin/apps/${app.id}`);
}

export async function updateAppAction(appId: string, formData: FormData) {
  await requireAuth();
  await ensureDatabase();
  const parsed = appSchema.parse({
    name: text(formData, "name"),
    packageName: text(formData, "packageName"),
    description: text(formData, "description"),
    status: text(formData, "status")
  });
  const iconUrl = await saveIcon(formData.get("icon") as File | null);
  await prisma.appProject.update({
    where: { id: appId },
    data: {
      ...parsed,
      ...(iconUrl ? { iconUrl } : {})
    }
  });
  revalidatePath("/");
  revalidatePath(`/apps/${appId}`);
  revalidatePath(`/admin/apps/${appId}`);
}

export async function uploadReleaseAction(appId: string, formData: FormData) {
  await requireAuth();
  await ensureDatabase();
  const app = await prisma.appProject.findUniqueOrThrow({ where: { id: appId } });
  const versionName = text(formData, "versionName");
  if (!versionName) throw new Error("请填写版本号");
  const apk = formData.get("apk") as File | null;
  if (!apk || apk.size === 0) throw new Error("请选择 APK 文件");

  const saved = await saveApk(apk, app.packageName, versionName);

  const release = await prisma.$transaction(async (tx) => {
    await tx.appRelease.updateMany({
      where: { appProjectId: appId },
      data: { isLatest: false }
    });

    const created = await tx.appRelease.create({
      data: {
        appProjectId: appId,
        versionName,
        versionCode: optionalInt(text(formData, "versionCode")),
        apkUrl: saved.url,
        apkFileName: saved.fileName,
        apkSize: saved.size,
        sha256: saved.sha256,
        changelog: text(formData, "changelog"),
        isLatest: true
      }
    });

    await tx.appProject.update({
      where: { id: appId },
      data: {
        latestVersionId: created.id,
        updatedAt: new Date()
      }
    });

    return created;
  });

  revalidatePath("/");
  revalidatePath(`/apps/${appId}`);
  revalidatePath(`/admin/apps/${appId}`);
  redirect(`/admin/apps/${appId}#release-${release.id}`);
}

export async function updateReleaseAction(releaseId: string, formData: FormData) {
  await requireAuth();
  await ensureDatabase();
  const release = await prisma.appRelease.update({
    where: { id: releaseId },
    data: {
      versionName: text(formData, "versionName"),
      versionCode: optionalInt(text(formData, "versionCode")),
      changelog: text(formData, "changelog")
    }
  });
  revalidatePath("/");
  revalidatePath(`/apps/${release.appProjectId}`);
  revalidatePath(`/admin/apps/${release.appProjectId}`);
}

export async function deleteReleaseAction(releaseId: string) {
  await requireAuth();
  await ensureDatabase();
  const release = await prisma.appRelease.findUniqueOrThrow({
    where: { id: releaseId }
  });

  await deleteApkFile(release.apkFileName);

  await prisma.$transaction(async (tx) => {
    await tx.appRelease.delete({ where: { id: releaseId } });
    if (release.isLatest) {
      const next = await tx.appRelease.findFirst({
        where: { appProjectId: release.appProjectId },
        orderBy: { createdAt: "desc" }
      });
      await tx.appRelease.updateMany({
        where: { appProjectId: release.appProjectId },
        data: { isLatest: false }
      });
      if (next) {
        await tx.appRelease.update({
          where: { id: next.id },
          data: { isLatest: true }
        });
      }
      await tx.appProject.update({
        where: { id: release.appProjectId },
        data: { latestVersionId: next?.id || null }
      });
    }
  });

  revalidatePath("/");
  revalidatePath(`/apps/${release.appProjectId}`);
  revalidatePath(`/admin/apps/${release.appProjectId}`);
}

export async function archiveAppAction(appId: string) {
  await requireAuth();
  await ensureDatabase();
  await prisma.appProject.update({
    where: { id: appId },
    data: { status: "ARCHIVED" }
  });
  revalidatePath("/");
  revalidatePath(`/apps/${appId}`);
  revalidatePath(`/admin/apps/${appId}`);
}
