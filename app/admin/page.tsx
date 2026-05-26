import Link from "next/link";
import { redirect } from "next/navigation";
import { createAppAction } from "@/app/actions";
import { AdminChrome } from "@/components/AdminChrome";
import { IconBox } from "@/components/IconBox";
import { StatusPill } from "@/components/StatusPill";
import { isAuthed } from "@/lib/auth";
import { APP_STATUSES, statusLabels } from "@/lib/constants";
import { formatDate } from "@/lib/format";
import { getApps } from "@/lib/store";

export default async function AdminPage() {
  if (!(await isAuthed())) redirect("/admin/login");
  const apps = await getApps();

  return (
    <AdminChrome>
      <section className="mx-auto grid max-w-7xl gap-6 px-4 py-6 lg:grid-cols-[420px_1fr]">
        <form
          action={createAppAction}
          encType="multipart/form-data"
          className="h-fit rounded-lg border border-line bg-panel p-4"
        >
          <h1 className="text-lg font-semibold">新增应用</h1>
          <div className="mt-4 grid gap-3">
            <label className="text-sm">
              <span className="text-muted">应用名称</span>
              <input name="name" required className="mt-1 h-10 w-full rounded-md border border-line bg-panel2 px-3" />
            </label>
            <label className="text-sm">
              <span className="text-muted">包名</span>
              <input
                name="packageName"
                required
                placeholder="com.example.app"
                className="mt-1 h-10 w-full rounded-md border border-line bg-panel2 px-3 font-mono"
              />
            </label>
            <label className="text-sm">
              <span className="text-muted">状态</span>
              <select name="status" className="mt-1 h-10 w-full rounded-md border border-line bg-panel2 px-3">
                {APP_STATUSES.map((status) => (
                  <option key={status} value={status}>
                    {statusLabels[status]}
                  </option>
                ))}
              </select>
            </label>
            <label className="text-sm">
              <span className="text-muted">图标</span>
              <input
                name="icon"
                type="file"
                accept=".png,.jpg,.jpeg,.webp,image/png,image/jpeg,image/webp"
                className="mt-1 w-full rounded-md border border-line bg-panel2 px-3 py-2 text-sm"
              />
            </label>
            <label className="text-sm">
              <span className="text-muted">说明</span>
              <textarea name="description" rows={4} className="mt-1 w-full rounded-md border border-line bg-panel2 p-3" />
            </label>
          </div>
          <button className="mt-4 h-10 w-full rounded-md bg-accent font-semibold text-black hover:bg-emerald-300">
            创建应用
          </button>
        </form>

        <section className="rounded-lg border border-line bg-panel p-4">
          <h2 className="text-lg font-semibold">应用仓库</h2>
          <div className="mt-4 grid gap-3">
            {apps.map((app) => (
              <Link
                key={app.id}
                href={`/admin/apps/${app.id}`}
                className="flex items-center gap-3 rounded-md border border-line bg-panel2 p-3 hover:border-accent"
              >
                <IconBox src={app.iconUrl} name={app.name} size="sm" />
                <div className="min-w-0 flex-1">
                  <div className="flex items-center gap-2">
                    <span className="truncate font-medium">{app.name}</span>
                    <StatusPill status={app.status} />
                  </div>
                  <div className="mt-1 truncate font-mono text-xs text-muted">{app.packageName}</div>
                </div>
                <div className="hidden text-right font-mono text-xs text-muted sm:block">
                  {formatDate(app.updatedAt)}
                </div>
              </Link>
            ))}
            {!apps.length ? (
              <div className="rounded-md border border-dashed border-line p-8 text-center text-muted">
                还没有应用。
              </div>
            ) : null}
          </div>
        </section>
      </section>
    </AdminChrome>
  );
}
