import Link from "next/link";
import { Download, ExternalLink } from "lucide-react";
import { AppWithLatest } from "@/lib/store";
import { formatBytes, formatDate } from "@/lib/format";
import { IconBox } from "@/components/IconBox";
import { StatusPill } from "@/components/StatusPill";

export function AppCard({ app }: { app: AppWithLatest }) {
  const latest = app.releases[0];

  return (
    <article className="rounded-lg border border-line bg-panel p-4 shadow-xl shadow-black/10">
      <div className="flex items-start gap-3">
        <IconBox src={app.iconUrl} name={app.name} />
        <div className="min-w-0 flex-1">
          <div className="flex items-start justify-between gap-2">
            <h2 className="truncate text-lg font-semibold">{app.name}</h2>
            <StatusPill status={app.status} />
          </div>
          <p className="mt-1 truncate font-mono text-xs text-muted">{app.packageName}</p>
        </div>
      </div>

      <p className="mt-4 line-clamp-2 min-h-10 text-sm leading-5 text-zinc-300">
        {app.description || "暂无说明"}
      </p>

      <div className="mt-4 grid grid-cols-2 gap-3 border-t border-line pt-4 text-xs text-muted">
        <div>
          <div>当前版本</div>
          <div className="mt-1 font-mono text-ink">{latest?.versionName || "未上传"}</div>
        </div>
        <div>
          <div>安装包大小</div>
          <div className="mt-1 font-mono text-ink">
            {latest ? formatBytes(latest.apkSize) : "-"}
          </div>
        </div>
        <div className="col-span-2">
          <div>最近更新</div>
          <div className="mt-1 font-mono text-ink">{formatDate(app.updatedAt)}</div>
        </div>
      </div>

      <div className="mt-4 flex gap-2">
        {latest ? (
          <a
            href={latest.apkUrl}
            className="inline-flex h-10 flex-1 items-center justify-center gap-2 rounded-md bg-accent px-3 text-sm font-medium text-black hover:bg-emerald-300"
          >
            <Download className="h-4 w-4" />
            下载 APK
          </a>
        ) : (
          <button
            disabled
            className="h-10 flex-1 rounded-md border border-line px-3 text-sm text-muted"
          >
            暂无 APK
          </button>
        )}
        <Link
          href={`/apps/${app.id}`}
          className="inline-flex h-10 items-center justify-center gap-2 rounded-md border border-line bg-panel2 px-3 text-sm hover:border-accent"
        >
          <ExternalLink className="h-4 w-4" />
          详情
        </Link>
      </div>
    </article>
  );
}
