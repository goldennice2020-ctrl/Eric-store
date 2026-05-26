import { Clock3, UploadCloud } from "lucide-react";
import { AppCard } from "@/components/AppCard";
import { TopBar } from "@/components/TopBar";
import { APP_STATUSES, statusLabels } from "@/lib/constants";
import { formatBytes, formatDate } from "@/lib/format";
import { getApps, getRecentReleases } from "@/lib/store";

export default async function Home({
  searchParams
}: {
  searchParams: Promise<{ q?: string; status?: string; sort?: "newest" | "oldest" }>;
}) {
  const params = await searchParams;
  const apps = await getApps({
    query: params.q,
    status: params.status,
    sort: params.sort
  });
  const recent = await getRecentReleases();

  return (
    <main>
      <TopBar query={params.q} />
      <section className="mx-auto grid max-w-7xl gap-6 px-4 py-6 lg:grid-cols-[1fr_320px]">
        <div>
          <div className="mb-5 flex flex-col gap-3 rounded-lg border border-line bg-panel/80 p-4 md:flex-row md:items-center md:justify-between">
            <div>
              <h1 className="text-2xl font-semibold">Eric Store</h1>
              <p className="mt-1 text-sm text-muted">私人应用分发中心</p>
            </div>
            <form className="flex flex-wrap gap-2" action="/">
              <input type="hidden" name="q" value={params.q || ""} />
              <select
                name="status"
                defaultValue={params.status || ""}
                className="h-10 rounded-md border border-line bg-panel2 px-3 text-sm"
              >
                <option value="">全部状态</option>
                {APP_STATUSES.map((status) => (
                  <option key={status} value={status}>
                    {statusLabels[status]}
                  </option>
                ))}
              </select>
              <select
                name="sort"
                defaultValue={params.sort || "newest"}
                className="h-10 rounded-md border border-line bg-panel2 px-3 text-sm"
              >
                <option value="newest">最近更新优先</option>
                <option value="oldest">较早更新优先</option>
              </select>
              <button className="h-10 rounded-md border border-line bg-panel2 px-3 text-sm hover:border-accent">
                应用筛选
              </button>
            </form>
          </div>

          {apps.length ? (
            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
              {apps.map((app) => (
                <AppCard key={app.id} app={app} />
              ))}
            </div>
          ) : (
            <div className="rounded-lg border border-dashed border-line bg-panel p-10 text-center text-muted">
              暂无应用。进入管理页新增应用并上传 APK。
            </div>
          )}
        </div>

        <aside className="h-fit rounded-lg border border-line bg-panel p-4">
          <div className="flex items-center gap-2 text-sm font-semibold">
            <Clock3 className="h-4 w-4 text-accent" />
            最近上传版本
          </div>
          <div className="mt-4 space-y-3">
            {recent.length ? (
              recent.map((release) => (
                <a
                  key={release.id}
                  href={release.apkUrl}
                  className="block rounded-md border border-line bg-panel2 p-3 hover:border-accent"
                >
                  <div className="flex items-center gap-2 text-sm">
                    <UploadCloud className="h-4 w-4 text-muted" />
                    <span className="truncate">{release.appProject.name}</span>
                  </div>
                  <div className="mt-2 flex justify-between gap-3 font-mono text-xs text-muted">
                    <span>{release.versionName}</span>
                    <span>{formatBytes(release.apkSize)}</span>
                  </div>
                  <div className="mt-1 font-mono text-xs text-muted">
                    {formatDate(release.createdAt)}
                  </div>
                </a>
              ))
            ) : (
              <p className="text-sm text-muted">还没有上传记录。</p>
            )}
          </div>
        </aside>
      </section>
    </main>
  );
}
