import Link from "next/link";
import { notFound } from "next/navigation";
import { Download, ShieldCheck } from "lucide-react";
import { IconBox } from "@/components/IconBox";
import { StatusPill } from "@/components/StatusPill";
import { TopBar } from "@/components/TopBar";
import { formatBytes, formatDate } from "@/lib/format";
import { getAppDetail } from "@/lib/store";

export default async function AppDetailPage({
  params
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const app = await getAppDetail(id);
  if (!app) notFound();
  const latest = app.releases.find((release) => release.isLatest) || app.releases[0];

  return (
    <main>
      <TopBar showSearch={false} />
      <section className="mx-auto max-w-5xl px-4 py-6">
        <Link href="/" className="text-sm text-muted hover:text-ink">
          返回应用列表
        </Link>

        <div className="mt-4 rounded-lg border border-line bg-panel p-5">
          <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
            <div className="flex min-w-0 gap-4">
              <IconBox src={app.iconUrl} name={app.name} size="lg" />
              <div className="min-w-0">
                <div className="flex flex-wrap items-center gap-3">
                  <h1 className="text-3xl font-semibold">{app.name}</h1>
                  <StatusPill status={app.status} />
                </div>
                <p className="mt-2 break-all font-mono text-sm text-muted">{app.packageName}</p>
                <p className="mt-3 max-w-2xl text-sm leading-6 text-zinc-300">
                  {app.description || "暂无说明"}
                </p>
              </div>
            </div>
            {latest ? (
              <a
                href={latest.apkUrl}
                className="inline-flex h-11 items-center justify-center gap-2 rounded-md bg-accent px-4 text-sm font-semibold text-black hover:bg-emerald-300"
              >
                <Download className="h-4 w-4" />
                下载最新版 APK
              </a>
            ) : null}
          </div>
        </div>

        {latest ? (
          <div className="mt-4 grid gap-4 md:grid-cols-2">
            <section className="rounded-lg border border-line bg-panel p-4">
              <h2 className="text-sm font-semibold">最新版本</h2>
              <dl className="mt-4 grid gap-3 text-sm">
                <div className="flex justify-between gap-3">
                  <dt className="text-muted">版本号</dt>
                  <dd className="font-mono">{latest.versionName}</dd>
                </div>
                <div className="flex justify-between gap-3">
                  <dt className="text-muted">版本代码</dt>
                  <dd className="font-mono">{latest.versionCode ?? "-"}</dd>
                </div>
                <div className="flex justify-between gap-3">
                  <dt className="text-muted">APK 大小</dt>
                  <dd className="font-mono">{formatBytes(latest.apkSize)}</dd>
                </div>
                <div className="flex justify-between gap-3">
                  <dt className="text-muted">上传时间</dt>
                  <dd className="font-mono">{formatDate(latest.createdAt)}</dd>
                </div>
              </dl>
            </section>
            <section className="rounded-lg border border-line bg-panel p-4">
              <div className="flex items-center gap-2 text-sm font-semibold">
                <ShieldCheck className="h-4 w-4 text-accent" />
                SHA256 校验值
              </div>
              <p className="mt-4 break-all rounded-md border border-line bg-panel2 p-3 font-mono text-xs text-zinc-300">
                {latest.sha256}
              </p>
              <p className="mt-3 text-xs leading-5 text-muted">
                安卓手机安装前可能需要允许当前浏览器“安装未知来源应用”。
              </p>
            </section>
          </div>
        ) : null}

        <section className="mt-4 rounded-lg border border-line bg-panel p-4">
          <h2 className="text-sm font-semibold">更新日志</h2>
          <div className="mt-4 whitespace-pre-wrap rounded-md border border-line bg-panel2 p-3 text-sm leading-6 text-zinc-300">
            {latest?.changelog || "暂无更新日志"}
          </div>
        </section>

        <section className="mt-4 rounded-lg border border-line bg-panel p-4">
          <h2 className="text-sm font-semibold">历史版本</h2>
          <div className="mt-4 overflow-x-auto">
            <table className="w-full min-w-[760px] text-left text-sm">
              <thead className="text-xs text-muted">
                <tr className="border-b border-line">
                  <th className="py-3">版本</th>
                  <th>版本代码</th>
                  <th>大小</th>
                  <th>上传时间</th>
                  <th>SHA256</th>
                  <th className="text-right">下载</th>
                </tr>
              </thead>
              <tbody>
                {app.releases.map((release) => (
                  <tr key={release.id} className="border-b border-line/70">
                    <td className="py-3 font-mono">
                      {release.versionName}
                      {release.isLatest ? (
                        <span className="ml-2 rounded border border-accent/40 px-1.5 py-0.5 text-xs text-accent">
                          latest
                        </span>
                      ) : null}
                    </td>
                    <td className="font-mono">{release.versionCode ?? "-"}</td>
                    <td className="font-mono">{formatBytes(release.apkSize)}</td>
                    <td className="font-mono text-xs">{formatDate(release.createdAt)}</td>
                    <td className="max-w-[260px] truncate font-mono text-xs text-muted">
                      {release.sha256}
                    </td>
                    <td className="text-right">
                      <a
                        href={release.apkUrl}
                        className="inline-flex h-9 items-center justify-center gap-2 rounded-md border border-line bg-panel2 px-3 hover:border-accent"
                      >
                        <Download className="h-4 w-4" />
                        APK
                      </a>
                    </td>
                  </tr>
                ))}
                {!app.releases.length ? (
                  <tr>
                    <td colSpan={6} className="py-8 text-center text-muted">
                      暂无版本。
                    </td>
                  </tr>
                ) : null}
              </tbody>
            </table>
          </div>
        </section>
      </section>
    </main>
  );
}
