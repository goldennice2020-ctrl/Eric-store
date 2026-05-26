import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Eric Store",
  description: "私人应用分发中心"
};

export default function RootLayout({
  children
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="zh-CN">
      <body>{children}</body>
    </html>
  );
}
