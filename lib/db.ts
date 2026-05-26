import { prisma } from "@/lib/prisma";

let initPromise: Promise<void> | null = null;

export async function ensureDatabase() {
  if (!initPromise) {
    initPromise = initDatabase();
  }
  return initPromise;
}

async function initDatabase() {
  await prisma.$executeRawUnsafe(`
    CREATE TABLE IF NOT EXISTS "AppProject" (
      "id" TEXT NOT NULL PRIMARY KEY,
      "name" TEXT NOT NULL,
      "packageName" TEXT NOT NULL,
      "description" TEXT NOT NULL,
      "iconUrl" TEXT,
      "status" TEXT NOT NULL DEFAULT 'DEVELOPING',
      "latestVersionId" TEXT,
      "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
      "updatedAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    )
  `);

  await prisma.$executeRawUnsafe(`
    CREATE UNIQUE INDEX IF NOT EXISTS "AppProject_packageName_key"
    ON "AppProject"("packageName")
  `);

  await prisma.$executeRawUnsafe(`
    CREATE TABLE IF NOT EXISTS "AppRelease" (
      "id" TEXT NOT NULL PRIMARY KEY,
      "appProjectId" TEXT NOT NULL,
      "versionName" TEXT NOT NULL,
      "versionCode" INTEGER,
      "apkUrl" TEXT NOT NULL,
      "apkFileName" TEXT NOT NULL,
      "apkSize" INTEGER NOT NULL,
      "sha256" TEXT NOT NULL,
      "changelog" TEXT NOT NULL,
      "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
      "isLatest" BOOLEAN NOT NULL DEFAULT false,
      CONSTRAINT "AppRelease_appProjectId_fkey"
        FOREIGN KEY ("appProjectId")
        REFERENCES "AppProject" ("id")
        ON DELETE CASCADE
        ON UPDATE CASCADE
    )
  `);

  await prisma.$executeRawUnsafe(`
    CREATE INDEX IF NOT EXISTS "AppRelease_appProjectId_createdAt_idx"
    ON "AppRelease"("appProjectId", "createdAt")
  `);
}
