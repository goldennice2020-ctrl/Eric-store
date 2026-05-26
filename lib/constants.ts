import path from "node:path";

export const MAX_APK_SIZE = 300 * 1024 * 1024;
export const UPLOAD_ROOT = process.env.UPLOAD_ROOT || path.join(process.cwd(), "uploads");
export const APK_DIR = path.join(UPLOAD_ROOT, "apks");
export const ICON_DIR = path.join(UPLOAD_ROOT, "icons");

export const APP_STATUSES = ["DEVELOPING", "AVAILABLE", "ARCHIVED"] as const;
export type AppStatus = (typeof APP_STATUSES)[number];

export const statusLabels = {
  DEVELOPING: "开发中",
  AVAILABLE: "可用",
  ARCHIVED: "已归档"
} as const;

export const statusTone = {
  DEVELOPING: "border-warn/40 bg-warn/10 text-warn",
  AVAILABLE: "border-accent/40 bg-accent/10 text-accent",
  ARCHIVED: "border-zinc-500/40 bg-zinc-500/10 text-zinc-300"
} as const;
