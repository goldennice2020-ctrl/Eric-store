import fs from "node:fs";
import path from "node:path";
import { notFound } from "next/navigation";
import { APK_DIR } from "@/lib/constants";

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ fileName: string }> }
) {
  const { fileName } = await params;
  const safeName = path.basename(fileName);
  const fullPath = path.join(APK_DIR, safeName);

  if (!fs.existsSync(fullPath)) {
    notFound();
  }

  const stat = fs.statSync(fullPath);
  const stream = fs.createReadStream(fullPath);

  return new Response(stream as unknown as BodyInit, {
    headers: {
      "Content-Type": "application/vnd.android.package-archive",
      "Content-Length": stat.size.toString(),
      "Content-Disposition": `attachment; filename="${safeName}"`,
      "Cache-Control": "private, max-age=0"
    }
  });
}
