import Link from "next/link";
import { LockKeyhole } from "lucide-react";

export function TopBar({
  query = "",
  showSearch = true
}: {
  query?: string;
  showSearch?: boolean;
}) {
  return (
    <header className="sticky top-0 z-10 border-b border-line/80 bg-[#0b0f13]/85 backdrop-blur">
      <div className="mx-auto flex max-w-7xl flex-col gap-3 px-4 py-4 md:flex-row md:items-center">
        <Link href="/" className="mr-2 shrink-0">
          <div className="text-xl font-semibold tracking-normal">Eric Store</div>
          <div className="text-xs text-muted">私人应用分发中心</div>
        </Link>
        {showSearch ? (
          <form className="flex min-w-0 flex-1 gap-2" action="/">
            <input
              name="q"
              defaultValue={query}
              placeholder="搜索应用名称或包名"
              className="h-10 min-w-0 flex-1 rounded-md border border-line bg-panel px-3 text-sm outline-none focus:border-accent"
            />
            <button className="h-10 rounded-md border border-line bg-panel2 px-4 text-sm hover:border-accent">
              搜索
            </button>
          </form>
        ) : null}
        <Link
          href="/admin"
          className="inline-flex h-10 items-center justify-center gap-2 rounded-md border border-line bg-panel2 px-3 text-sm hover:border-accent"
        >
          <LockKeyhole className="h-4 w-4" />
          管理
        </Link>
      </div>
    </header>
  );
}
