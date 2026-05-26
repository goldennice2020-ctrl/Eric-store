import Link from "next/link";
import { LockKeyhole, Search } from "lucide-react";

export function TopBar({
  query = "",
  showSearch = true
}: {
  query?: string;
  showSearch?: boolean;
}) {
  return (
    <header className="sticky top-0 z-10 border-b border-white/5 bg-[#17161c]/90 backdrop-blur-xl">
      <div className="mx-auto flex max-w-4xl flex-col gap-3 px-4 py-4 md:flex-row md:items-center">
        <Link href="/" className="mr-2 shrink-0">
          <div className="text-xl font-semibold tracking-normal text-zinc-50">Eric Store</div>
          <div className="text-xs text-zinc-500">私人应用分发中心</div>
        </Link>
        {showSearch ? (
          <form className="relative flex min-w-0 flex-1" action="/">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-zinc-500" />
            <input
              name="q"
              defaultValue={query}
              placeholder="搜索应用"
              className="h-11 min-w-0 flex-1 rounded-full border border-white/5 bg-[#24232a] pl-10 pr-4 text-sm text-zinc-100 outline-none placeholder:text-zinc-500 focus:border-blue-500/60"
            />
          </form>
        ) : null}
        <Link
          href="/admin"
          className="inline-flex h-10 items-center justify-center gap-2 rounded-full bg-[#24232a] px-4 text-sm text-zinc-200 hover:bg-[#2d2c34]"
        >
          <LockKeyhole className="h-4 w-4" />
          管理
        </Link>
      </div>
    </header>
  );
}
