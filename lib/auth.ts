import crypto from "node:crypto";
import { cookies } from "next/headers";

const COOKIE_NAME = "eric_store_admin";

function getSecret() {
  return process.env.SESSION_SECRET || "dev-eric-store-secret";
}

function sign(value: string) {
  return crypto.createHmac("sha256", getSecret()).update(value).digest("hex");
}

function safeEqual(a: string, b: string) {
  const left = Buffer.from(a);
  const right = Buffer.from(b);
  return left.length === right.length && crypto.timingSafeEqual(left, right);
}

export async function isAuthed() {
  const jar = await cookies();
  const token = jar.get(COOKIE_NAME)?.value;
  if (!token) return false;
  const [value, signature] = token.split(".");
  if (!value || !signature) return false;
  return safeEqual(sign(value), signature);
}

export async function requireAuth() {
  if (!(await isAuthed())) {
    throw new Error("Unauthorized");
  }
}

export async function setAdminSession() {
  const value = Date.now().toString();
  const jar = await cookies();
  jar.set(COOKIE_NAME, `${value}.${sign(value)}`, {
    httpOnly: true,
    sameSite: "lax",
    secure: process.env.NODE_ENV === "production",
    maxAge: 60 * 60 * 24 * 30,
    path: "/"
  });
}

export async function clearAdminSession() {
  const jar = await cookies();
  jar.delete(COOKIE_NAME);
}

export function verifyPassword(password: string) {
  const expected = process.env.ADMIN_PASSWORD || "change-me";
  return safeEqual(password, expected);
}

export function verifyUploadToken(token: string) {
  const expected = process.env.UPLOAD_TOKEN || "";
  return Boolean(expected) && safeEqual(token, expected);
}
