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
  const activeStatus = params.status || "";
  const activeSort = params.sort || "newest";

  return (
    <main className="min-h-screen bg-[#17161c]">
      <TopBar query={params.q} />
      <section className="mx-auto max-w-4xl px-5 pb-12 pt-5">
        <div className="mb-3">
          <p className="text-sm font-medium text-[#1684ff]">PRIVATE STORE</p>
          <h1 className="mt-1 text-[34px] font-bold leading-tight text-zinc-50 md:text-[44px]">
            Eric Store
          </h1>
          <p className="mt-1 text-lg text-zinc-500">私人应用分发中心</p>
        </div>

        <form className="mb-3 flex gap-2 overflow-x-auto py-2" action="/">
          <input type="hidden" name="q" value={params.q || ""} />
          <input type="hidden" name="sort" value={activeSort} />
          <button
            name="status"
            value=""
            className={`h-10 shrink-0 rounded-full px-4 text-sm font-medium ${
              activeStatus === "" ? "bg-zinc-100 text-zinc-950" : "bg-[#24232a] text-zinc-400"
            }`}
          >
            全部
          </button>
          {APP_STATUSES.map((status) => (
            <button
              key={status}
              name="status"
              value={status}
              className={`h-10 shrink-0 rounded-full px-4 text-sm font-medium ${
                activeStatus === status
                  ? "bg-zinc-100 text-zinc-950"
                  : "bg-[#24232a] text-zinc-400"
              }`}
            >
              {statusLabels[status]}
            </button>
          ))}
        </form>

        <form className="mb-5 flex justify-end" action="/">
          <input type="hidden" name="q" value={params.q || ""} />
          <input type="hidden" name="status" value={activeStatus} />
          <select
            name="sort"
            defaultValue={activeSort}
            className="h-10 rounded-full border border-white/5 bg-[#24232a] px-4 text-sm text-zinc-300 outline-none"
          >
            <option value="newest">最近更新</option>
            <option value="oldest">较早更新</option>
          </select>
          <button className="ml-2 h-10 rounded-full bg-[#24232a] px-4 text-sm text-zinc-300">
            排序
          </button>
        </form>

        {apps.length ? (
          <div className="rounded-[26px] bg-[#1c1b22] px-4 shadow-2xl shadow-black/20 md:px-6">
            {apps.map((app) => (
              <AppCard key={app.id} app={app} />
            ))}
          </div>
        ) : (
          <div className="rounded-[26px] border border-dashed border-white/10 bg-[#1c1b22] p-10 text-center text-zinc-500">
            暂无应用。进入管理页新增应用并上传 APK。
          </div>
        )}

        <section className="mt-8">
          <div className="mb-3">
            <h2 className="text-xl font-semibold text-zinc-100">最近上传</h2>
            <p className="mt-1 text-sm text-zinc-500">新版本会自动显示在这里</p>
          </div>
          <div className="space-y-2">
            {recent.length ? (
              recent.map((release) => (
                <a
                  key={release.id}
                  href={release.apkUrl}
                  className="flex items-center justify-between gap-4 rounded-2xl bg-[#1c1b22] px-4 py-3"
                >
                  <div className="min-w-0">
                    <div className="truncate text-base font-medium text-zinc-100">
                      {release.appProject.name}
                    </div>
                    <div className="mt-1 truncate font-mono text-xs text-zinc-500">
                      {release.versionName} · {formatDate(release.createdAt)}
                    </div>
                  </div>
                  <div className="shrink-0 font-mono text-sm text-zinc-500">
                    {formatBytes(release.apkSize)}
                  </div>
                </a>
              ))
            ) : (
              <div className="rounded-2xl bg-[#1c1b22] px-4 py-5 text-sm text-zinc-500">
                还没有上传记录。
              </div>
            )}
          </div>
        </section>
      </section>
    </main>
  );
}
