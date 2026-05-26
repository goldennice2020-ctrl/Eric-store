import { loginAction } from "@/app/actions";

export default async function LoginPage({
  searchParams
}: {
  searchParams: Promise<{ error?: string }>;
}) {
  const params = await searchParams;

  return (
    <main className="flex min-h-screen items-center justify-center px-4">
      <form
        action={loginAction}
        className="w-full max-w-sm rounded-lg border border-line bg-panel p-5 shadow-2xl shadow-black/20"
      >
        <h1 className="text-2xl font-semibold">Eric Store</h1>
        <p className="mt-1 text-sm text-muted">输入管理密码</p>
        <input
          type="password"
          name="password"
          required
          autoFocus
          className="mt-5 h-11 w-full rounded-md border border-line bg-panel2 px-3 outline-none focus:border-accent"
          placeholder="管理密码"
        />
        {params.error ? (
          <p className="mt-3 rounded-md border border-red-400/30 bg-red-400/10 px-3 py-2 text-sm text-red-200">
            密码不正确
          </p>
        ) : null}
        <button className="mt-5 h-11 w-full rounded-md bg-accent font-semibold text-black hover:bg-emerald-300">
          登录
        </button>
      </form>
    </main>
  );
}
