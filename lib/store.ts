import { Prisma } from "@prisma/client";
import { APP_STATUSES, AppStatus } from "@/lib/constants";
import { ensureDatabase } from "@/lib/db";
import { prisma } from "@/lib/prisma";

export type AppWithLatest = Prisma.AppProjectGetPayload<{
  include: {
    releases: {
      orderBy: { createdAt: "desc" };
      take: 1;
    };
  };
}>;

export async function getApps(options?: {
  query?: string;
  status?: string;
  sort?: "newest" | "oldest";
}) {
  await ensureDatabase();
  const where: Prisma.AppProjectWhereInput = {
    ...(options?.query
      ? {
          OR: [
            { name: { contains: options.query } },
            { packageName: { contains: options.query } }
          ]
        }
      : {}),
    ...(options?.status &&
    APP_STATUSES.includes(options.status as AppStatus)
      ? { status: options.status as AppStatus }
      : {})
  };

  return prisma.appProject.findMany({
    where,
    orderBy: { updatedAt: options?.sort === "oldest" ? "asc" : "desc" },
    include: {
      releases: {
        orderBy: { createdAt: "desc" },
        take: 1
      }
    }
  });
}

export async function getAppDetail(id: string) {
  await ensureDatabase();
  return prisma.appProject.findUnique({
    where: { id },
    include: {
      releases: {
        orderBy: { createdAt: "desc" }
      }
    }
  });
}

export async function getRecentReleases(take = 6) {
  await ensureDatabase();
  return prisma.appRelease.findMany({
    take,
    orderBy: { createdAt: "desc" },
    include: {
      appProject: true
    }
  });
}
