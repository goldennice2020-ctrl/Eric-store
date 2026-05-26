import fs from "node:fs";
import path from "node:path";
import { notFound } from "next/navigation";
import { ICON_DIR } from "@/lib/constants";

const contentTypes: Record<string, string> = {
  ".png": "image/png",
  ".jpg": "image/jpeg",
  ".jpeg": "image/jpeg",
  ".webp": "image/webp"
};

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ fileName: string }> }
) {
  const { fileName } = await params;
  const safeName = path.basename(fileName);
  const fullPath = path.join(ICON_DIR, safeName);

  if (!fs.existsSync(fullPath)) {
    notFound();
  }

  const stream = fs.createReadStream(fullPath);
  const ext = path.extname(safeName).toLowerCase();

  return new Response(stream as unknown as BodyInit, {
    headers: {
      "Content-Type": contentTypes[ext] || "application/octet-stream",
      "Cache-Control": "private, max-age=86400"
    }
  });
}
