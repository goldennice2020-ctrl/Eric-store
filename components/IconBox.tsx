import { Boxes } from "lucide-react";

export function IconBox({
  src,
  name,
  size = "md"
}: {
  src?: string | null;
  name: string;
  size?: "sm" | "md" | "lg";
}) {
  const className =
    size === "lg"
      ? "h-20 w-20"
      : size === "sm"
        ? "h-11 w-11"
        : "h-14 w-14";

  return (
    <div
      className={`${className} flex shrink-0 items-center justify-center overflow-hidden rounded-lg border border-line bg-panel2`}
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
