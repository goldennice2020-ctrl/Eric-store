# Eric Store 公网部署

目标：电脑关机、手机在外地时，仍然可以打开 Eric Store 并下载 APK。

## 推荐架构

- 一台长期在线的云服务器、家里 NAS 公网入口，或类似的常驻主机
- 一个域名，例如 `store.example.com`
- HTTPS 反向代理，例如 Caddy、Nginx、宝塔面板、1Panel
- Eric Store 使用 Docker 运行
- SQLite 数据库和 APK 文件保存在服务器的 `./data` 目录

## 服务器启动

把项目放到服务器后，在项目目录创建 `.env`：

```env
ADMIN_PASSWORD="你的后台密码"
SESSION_SECRET="换成很长的随机字符串"
UPLOAD_TOKEN="给 Codex 上传用的长 token"
```

启动：

```bash
docker compose up -d --build
```

服务会监听服务器本机的 `3002` 端口。

## 域名反向代理

把你的域名反向代理到：

```text
http://127.0.0.1:3002
```

反向代理需要允许大文件上传，至少 `300MB`。

如果用 Caddy，可以类似这样：

```caddyfile
store.example.com {
  reverse_proxy 127.0.0.1:3002
}
```

## Codex 自动上传地址

公网部署后，Codex 上传地址就是：

```text
https://store.example.com/api/codex/upload
```

上传命令：

```bash
ERIC_STORE_URL="https://store.example.com" \
ERIC_STORE_UPLOAD_TOKEN="你的 UPLOAD_TOKEN" \
bash scripts/codex-upload.sh \
  "/path/to/app.apk" \
  "应用名称" \
  "com.example.app" \
  "1.0.0" \
  "1"
```

## 重新构建手机 App 指向公网地址

把 `https://store.example.com` 换成你的真实域名：

```bash
cd android-app
ERIC_STORE_APP_URL="https://store.example.com" \
/Users/eric/.gradle/wrapper/dists/gradle-8.9-bin/90cnw93cvbtalezasaz0blq0a/gradle-8.9/bin/gradle assembleDebug
```

安装：

```bash
adb install -r -t -g app/build/outputs/apk/debug/app-debug.apk
```

这样手机 App 就不再依赖电脑、局域网或 USB 反向代理。
