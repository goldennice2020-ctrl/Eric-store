import crypto from "node:crypto";
import fs from "node:fs/promises";
import path from "node:path";
import { APK_DIR, ICON_DIR, MAX_APK_SIZE } from "@/lib/constants";

export function sanitizeFilePart(value: string) {
  return value
    .toLowerCase()
    .replace(/[^a-z0-9._-]+/g, "-")
    .replace(/^-+|-+$/g, "")
    .slice(0, 80);
}

export async function ensureStorageDirs() {
  await fs.mkdir(APK_DIR, { recursive: true });
  await fs.mkdir(ICON_DIR, { recursive: true });
}

export async function saveApk(file: File, packageName: string, versionName: string) {
  if (!file.name.toLowerCase().endsWith(".apk")) {
    throw new Error("只允许上传 .apk 文件");
  }

  if (file.size <= 0) {
    throw new Error("APK 文件为空");
  }

  if (file.size > MAX_APK_SIZE) {
    throw new Error("单个 APK 最大 300MB");
  }

  await ensureStorageDirs();
  const bytes = Buffer.from(await file.arrayBuffer());
  const sha256 = crypto.createHash("sha256").update(bytes).digest("hex");
  const safePackage = sanitizeFilePart(packageName || "app");
  const safeVersion = sanitizeFilePart(versionName || "version");
  const fileName = `${safePackage}-${safeVersion}-${Date.now()}.apk`;
  const fullPath = path.join(APK_DIR, fileName);

  await fs.writeFile(fullPath, bytes);

  return {
    fileName,
    size: bytes.length,
    sha256,
    url: `/downloads/apks/${fileName}`
  };
}

export async function saveIcon(file: File | null) {
  if (!file || file.size === 0) return null;

  const allowed = [".png", ".jpg", ".jpeg", ".webp"];
  const ext = path.extname(file.name).toLowerCase();
  if (!allowed.includes(ext)) {
    throw new Error("图标只支持 PNG/JPG/WebP");
  }

  await ensureStorageDirs();
  const bytes = Buffer.from(await file.arrayBuffer());
  const fileName = `icon-${Date.now()}-${sanitizeFilePart(file.name)}`;
  await fs.writeFile(path.join(ICON_DIR, fileName), bytes);
  return `/downloads/icons/${fileName}`;
}

export async function deleteApkFile(fileName: string) {
  const safeName = path.basename(fileName);
  await fs.rm(path.join(APK_DIR, safeName), { force: true });
}
