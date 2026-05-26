import Link from "next/link";
import { LogOut } from "lucide-react";
import { logoutAction } from "@/app/actions";

export function AdminChrome({ children }: { children: React.ReactNode }) {
  return (
    <main>
      <header className="border-b border-line bg-[#0b0f13]/90">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-4">
          <Link href="/admin">
            <div className="text-lg font-semibold">Eric Store Admin</div>
            <div className="text-xs text-muted">私人 APK 管理台</div>
          </Link>
          <div className="flex gap-2">
            <Link
              href="/"
              className="inline-flex h-10 items-center rounded-md border border-line bg-panel2 px-3 text-sm hover:border-accent"
            >
              前台
            </Link>
            <form action={logoutAction}>
              <button className="inline-flex h-10 items-center gap-2 rounded-md border border-line bg-panel2 px-3 text-sm hover:border-accent">
                <LogOut className="h-4 w-4" />
                退出
              </button>
            </form>
          </div>
        </div>
      </header>
      {children}
    </main>
  );
}
