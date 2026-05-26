import { Boxes } from "lucide-react";

export function IconBox({
  src,
  name,
  size = "md"
}: {
  src?: string | null;
  name: string;
  size?: "sm" | "md" | "lg" | "store";
}) {
  const className =
    size === "store"
      ? "h-[82px] w-[82px] rounded-[22px] md:h-[96px] md:w-[96px] md:rounded-[26px]"
      : size === "lg"
      ? "h-20 w-20"
      : size === "sm"
        ? "h-11 w-11"
        : "h-14 w-14";

  return (
    <div
      className={`${className} flex shrink-0 items-center justify-center overflow-hidden border border-white/10 bg-panel2 shadow-lg shadow-black/20`}
    >
      {src ? (
        // eslint-disable-next-line @next/next/no-img-element
        <img src={src} alt={`${name} 图标`} className="h-full w-full object-cover" />
      ) : (
        <Boxes className="h-1/2 w-1/2 text-accent" aria-hidden="true" />
      )}
    </div>
  );
}
