import Link from "next/link";
import { AppWithLatest } from "@/lib/store";
import { formatBytes } from "@/lib/format";
import { IconBox } from "@/components/IconBox";

export function AppCard({ app }: { app: AppWithLatest }) {
  const latest = app.releases[0];

  return (
    <article className="group flex min-h-[118px] items-center gap-4 border-b border-white/6 py-4 md:min-h-[132px] md:gap-5 md:py-5">
      <Link href={`/apps/${app.id}`} className="flex min-w-0 flex-1 items-center gap-4 md:gap-5">
        <IconBox src={app.iconUrl} name={app.name} size="store" />
        <div className="min-w-0 flex-1">
          <h2 className="truncate text-[24px] font-semibold leading-tight text-zinc-50 md:text-[30px]">
            {app.name}
          </h2>
          <p className="mt-1 font-mono text-[17px] leading-snug text-zinc-500 md:text-[20px]">
            {latest ? formatBytes(latest.apkSize) : "暂无 APK"}
          </p>
          <p className="mt-1 line-clamp-1 text-[17px] leading-snug text-zinc-500 md:text-[20px]">
            {app.description || app.packageName}
          </p>
        </div>
      </Link>

      <div className="shrink-0">
        {latest ? (
          <a
            href={latest.apkUrl}
            className="inline-flex h-12 min-w-[96px] items-center justify-center rounded-full bg-[#27262e] px-6 text-[20px] font-semibold text-[#1684ff] transition hover:bg-[#31303a] md:h-14 md:min-w-[116px] md:text-[24px]"
          >
            安装
          </a>
        ) : (
          <button
            disabled
            className="inline-flex h-12 min-w-[96px] items-center justify-center rounded-full bg-[#27262e] px-5 text-[18px] font-semibold text-zinc-500 md:h-14 md:min-w-[116px]"
          >
            等待
          </button>
        )}
      </div>
    </article>
  );
}
