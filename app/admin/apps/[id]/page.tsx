import Link from "next/link";
import { notFound, redirect } from "next/navigation";
import {
  archiveAppAction,
  deleteReleaseAction,
  updateAppAction,
  updateReleaseAction,
  uploadReleaseAction
} from "@/app/actions";
import { AdminChrome } from "@/components/AdminChrome";
import { IconBox } from "@/components/IconBox";
import { StatusPill } from "@/components/StatusPill";
import { isAuthed } from "@/lib/auth";
import { APP_STATUSES, statusLabels } from "@/lib/constants";
import { formatBytes, formatDate } from "@/lib/format";
import { getAppDetail } from "@/lib/store";

export default async function AdminAppPage({
  params
}: {
  params: Promise<{ id: string }>;
}) {
  if (!(await isAuthed())) redirect("/admin/login");
  const { id } = await params;
  const app = await getAppDetail(id);
  if (!app) notFound();

  const updateApp = updateAppAction.bind(null, app.id);
  const uploadRelease = uploadReleaseAction.bind(null, app.id);
  const archiveApp = archiveAppAction.bind(null, app.id);

  return (
    <AdminChrome>
      <section className="mx-auto max-w-7xl px-4 py-6">
        <Link href="/admin" className="text-sm text-muted hover:text-ink">
          返回管理台
        </Link>

        <div className="mt-4 flex flex-col gap-4 rounded-lg border border-line bg-panel p-4 md:flex-row md:items-center md:justify-between">
          <div className="flex items-center gap-3">
            <IconBox src={app.iconUrl} name={app.name} />
            <div>
              <div className="flex items-center gap-3">
                <h1 className="text-2xl font-semibold">{app.name}</h1>
                <StatusPill status={app.status} />
              </div>
              <p className="mt-1 break-all font-mono text-xs text-muted">{app.packageName}</p>
            </div>
          </div>
          <div className="flex gap-2">
            <Link
              href={`/apps/${app.id}`}
              className="inline-flex h-10 items-center rounded-md border border-line bg-panel2 px-3 text-sm hover:border-accent"
            >
              查看详情
            </Link>
            <form action={archiveApp}>
              <button className="h-10 rounded-md border border-line bg-panel2 px-3 text-sm hover:border-warn">
                归档应用
              </button>
            </form>
          </div>
        </div>

        <div className="mt-4 grid gap-4 lg:grid-cols-2">
          <form
            action={updateApp}
            encType="multipart/form-data"
            className="rounded-lg border border-line bg-panel p-4"
          >
            <h2 className="text-sm font-semibold">编辑应用信息</h2>
            <div className="mt-4 grid gap-3">
              <label className="text-sm">
                <span className="text-muted">应用名称</span>
                <input
                  name="name"
                  required
                  defaultValue={app.name}
                  className="mt-1 h-10 w-full rounded-md border border-line bg-panel2 px-3"
                />
              </label>
              <label className="text-sm">
                <span className="text-muted">包名</span>
                <input
                  name="packageName"
                  required
                  defaultValue={app.packageName}
                  className="mt-1 h-10 w-full rounded-md border border-line bg-panel2 px-3 font-mono"
                />
              </label>
              <label className="text-sm">
                <span className="text-muted">状态</span>
                <select
                  name="status"
                  defaultValue={app.status}
                  className="mt-1 h-10 w-full rounded-md border border-line bg-panel2 px-3"
                >
                  {APP_STATUSES.map((status) => (
                    <option key={status} value={status}>
                      {statusLabels[status]}
                    </option>
                  ))}
                </select>
              </label>
              <label className="text-sm">
                <span className="text-muted">替换图标</span>
                <input
                  name="icon"
                  type="file"
                  accept=".png,.jpg,.jpeg,.webp,image/png,image/jpeg,image/webp"
                  className="mt-1 w-full rounded-md border border-line bg-panel2 px-3 py-2 text-sm"
                />
              </label>
              <label className="text-sm">
                <span className="text-muted">说明</span>
                <textarea
                  name="description"
                  rows={5}
                  defaultValue={app.description}
                  className="mt-1 w-full rounded-md border border-line bg-panel2 p-3"
                />
              </label>
            </div>
            <button className="mt-4 h-10 rounded-md bg-accent px-4 font-semibold text-black hover:bg-emerald-300">
              保存信息
            </button>
          </form>

          <form
            action={uploadRelease}
            encType="multipart/form-data"
            className="rounded-lg border border-line bg-panel p-4"
          >
            <h2 className="text-sm font-semibold">上传新版本</h2>
            <div className="mt-4 grid gap-3">
              <label className="text-sm">
                <span className="text-muted">版本号</span>
                <input
                  name="versionName"
                  required
                  placeholder="1.0.0"
                  className="mt-1 h-10 w-full rounded-md border border-line bg-panel2 px-3 font-mono"
                />
              </label>
              <label className="text-sm">
                <span className="text-muted">版本代码</span>
                <input
                  name="versionCode"
                  inputMode="numeric"
                  placeholder="1"
                  className="mt-1 h-10 w-full rounded-md border border-line bg-panel2 px-3 font-mono"
                />
              </label>
              <label className="text-sm">
                <span className="text-muted">APK 文件</span>
                <input
                  name="apk"
                  type="file"
                  required
                  accept=".apk,application/vnd.android.package-archive"
                  className="mt-1 w-full rounded-md border border-line bg-panel2 px-3 py-2 text-sm"
                />
              </label>
              <label className="text-sm">
                <span className="text-muted">更新日志</span>
                <textarea
                  name="changelog"
                  rows={6}
                  className="mt-1 w-full rounded-md border border-line bg-panel2 p-3"
                />
              </label>
            </div>
            <p className="mt-3 text-xs text-muted">
              只接受 .apk，最大 300MB。上传后自动计算大小和 SHA256，并设为 latest。
            </p>
            <button className="mt-4 h-10 rounded-md bg-accent px-4 font-semibold text-black hover:bg-emerald-300">
              上传 APK
            </button>
          </form>
        </div>

        <section className="mt-4 rounded-lg border border-line bg-panel p-4">
          <h2 className="text-sm font-semibold">版本管理</h2>
          <div className="mt-4 grid gap-4">
            {app.releases.map((release) => {
              const updateRelease = updateReleaseAction.bind(null, release.id);
              const deleteRelease = deleteReleaseAction.bind(null, release.id);
              return (
                <div
                  id={`release-${release.id}`}
                  key={release.id}
                  className="rounded-md border border-line bg-panel2 p-4"
                >
                  <div className="flex flex-col gap-3 md:flex-row md:items-start md:justify-between">
                    <div>
                      <div className="font-mono text-sm">
                        {release.versionName}
                        {release.isLatest ? (
                          <span className="ml-2 rounded border border-accent/40 px-1.5 py-0.5 text-xs text-accent">
                            latest
                          </span>
                        ) : null}
                      </div>
                      <div className="mt-2 grid gap-1 font-mono text-xs text-muted">
                        <span>{release.apkFileName}</span>
                        <span>
                          {formatBytes(release.apkSize)} · {formatDate(release.createdAt)}
                        </span>
                        <span className="break-all">SHA256 {release.sha256}</span>
                      </div>
                    </div>
                    <div className="flex gap-2">
                      <a
                        href={release.apkUrl}
                        className="inline-flex h-9 items-center rounded-md border border-line bg-panel px-3 text-sm hover:border-accent"
                      >
                        下载
                      </a>
                      <form action={deleteRelease}>
                        <button className="h-9 rounded-md border border-red-400/30 bg-red-400/10 px-3 text-sm text-red-200 hover:border-red-300">
                          删除 APK
                        </button>
                      </form>
                    </div>
                  </div>
                  <form action={updateRelease} className="mt-4 grid gap-3 md:grid-cols-[1fr_120px]">
                    <label className="text-sm">
                      <span className="text-muted">版本号</span>
                      <input
                        name="versionName"
                        required
                        defaultValue={release.versionName}
                        className="mt-1 h-10 w-full rounded-md border border-line bg-panel px-3 font-mono"
                      />
                    </label>
                    <label className="text-sm">
                      <span className="text-muted">版本代码</span>
                      <input
                        name="versionCode"
                        defaultValue={release.versionCode ?? ""}
                        className="mt-1 h-10 w-full rounded-md border border-line bg-panel px-3 font-mono"
                      />
                    </label>
                    <label className="text-sm md:col-span-2">
                      <span className="text-muted">更新日志</span>
                      <textarea
                        name="changelog"
                        rows={4}
                        defaultValue={release.changelog}
                        className="mt-1 w-full rounded-md border border-line bg-panel p-3"
                      />
                    </label>
                    <button className="h-10 rounded-md border border-line bg-panel px-4 text-sm hover:border-accent md:w-fit">
                      保存版本信息
                    </button>
                  </form>
                </div>
              );
            })}
            {!app.releases.length ? (
              <div className="rounded-md border border-dashed border-line p-8 text-center text-muted">
                还没有上传 APK。
              </div>
            ) : null}
          </div>
        </section>
      </section>
    </AdminChrome>
  );
}
