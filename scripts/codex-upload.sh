#!/usr/bin/env bash
set -euo pipefail

STORE_URL="${ERIC_STORE_URL:-http://127.0.0.1:3002}"
UPLOAD_TOKEN="${ERIC_STORE_UPLOAD_TOKEN:-eric-store-upload-token}"

if [ "$#" -lt 4 ]; then
  echo "Usage: $0 <apk-path> <name> <package-name> <version-name> [version-code]" >&2
  exit 1
fi

APK_PATH="$1"
NAME="$2"
PACKAGE_NAME="$3"
VERSION_NAME="$4"
VERSION_CODE="${5:-}"

curl -sS \
  -X POST "$STORE_URL/api/codex/upload" \
  -H "Authorization: Bearer $UPLOAD_TOKEN" \
  -F "apk=@${APK_PATH}" \
  -F "name=${NAME}" \
  -F "packageName=${PACKAGE_NAME}" \
  -F "versionName=${VERSION_NAME}" \
  -F "versionCode=${VERSION_CODE}" \
  -F "status=AVAILABLE" \
  -F "description=由 Codex 自动上传" \
  -F "changelog=Codex 构建并上传的新版本"
