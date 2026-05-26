import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  allowedDevOrigins: [
    "127.0.0.1",
    "127.0.0.1:3002",
    "http://127.0.0.1:3002",
    "localhost",
    "localhost:3002",
    "http://localhost:3002",
    "192.168.110.96",
    "192.168.110.96:3002"
  ],
  experimental: {
    serverActions: {
      bodySizeLimit: "310mb"
    }
  }
};

export default nextConfig;
