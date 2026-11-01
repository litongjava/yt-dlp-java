# yt-dlp-server

基于 Tio Boot 和 [yt-dlp-java](../yt-dlp-java/readme.md) 的 YouTube MP3/MP4 下载示例。请求传入视频 ID，同步下载或复用本地文件，再返回音频或视频，支持缓存协商和 Range 请求。

## 构建与启动

需要 JDK 21（代码使用虚拟线程）、Maven、FFmpeg/ffprobe，以及可访问目标网站的网络。Maven 需能解析 `nexus.io:tio-boot:2.1.8` 及相关依赖。

从仓库根目录执行：

```sh
mvn clean package -Pproduction -DskipTests
cd yt-dlp-server
java -Dserver.port=8080 -jar target/yt-dlp-server-1.0.0.jar
```

`production` profile 生成包含依赖的可执行 JAR；默认 profile 不执行 repackage。`assembly` profile 引用的描述文件未提供，不作为推荐入口。

框架默认端口为 80，示例显式使用 8080。端口配置不限制监听地址，请在防火墙保护的开发环境运行。工作目录必须可写，用于保存下载文件、日志和 yt-dlp。

通过 `YT_DLP_BIN_WIN`、`YT_DLP_BIN_LINUX`、`YT_DLP_BIN_MAC` 指向现有 yt-dlp，`YT_DLP_PROXY` 配置代理，`YT_DLP_COOKIES` 指向外部 cookies 文件，`DENO_BIN_*` 配置 Deno。详见 [库配置](../yt-dlp-java/readme.md#配置)。

## Docker

镜像基于 `debian:trixie-slim`，包含 JRE 21、FFmpeg、yt-dlp 独立二进制和 Deno（YouTube 提取所需的 JS 运行时）。yt-dlp 和 Deno 在构建时从 GitHub Releases 下载并校验 SHA-256，支持 amd64 和 arm64。先在仓库根目录打包，再在本目录构建：

```sh
mvn clean package -Pproduction -DskipTests
cd yt-dlp-server
docker build -t yt-dlp-server .
docker run -d --name yt-dlp-server -p 8080:8080 -v yt-dlp-download:/app/download yt-dlp-server
```

| 构建参数 | 默认值 | 说明 |
| --- | --- | --- |
| `DEBIAN_MIRROR` | `mirrors.ustc.edu.cn` | apt 镜像，海外可用 `deb.debian.org` |
| `YT_DLP_VERSION` | `latest` | yt-dlp 发行版标签，如 `2026.10.01` |
| `DENO_VERSION` | `latest` | Deno 发行版标签，如 `v2.5.0` |

运行时可通过环境变量设置 `SERVER_PORT`、`JAVA_OPTS`、`YT_DLP_PROXY`、`YT_DLP_COOKIES`（需挂载 cookies 文件）。下载缓存位于卷 `/app/download`，容器以 uid 1000 运行。健康检查请求不带 id 的 `/mp3`，期望返回 400，不会触发下载。容器内更新 yt-dlp：`docker exec yt-dlp-server java -jar /opt/yt-dlp-server/app.jar --update-yt-dlp`，重建镜像后会恢复为构建时版本。

放在 nginx 后面时，零拷贝文件响应（不小于 1MB）自带 `X-Accel-Buffering: no`，nginx 不会把大文件缓冲到 `proxy_temp`。该头由 tio-boot 添加，可用 `SERVER_HTTP_FILE_DISABLE_PROXY_BUFFERING=false` 关闭。由于首次请求要同步等待下载，建议设置 `proxy_read_timeout 600s`。

## HTTP 接口

| 路径 | 行为 |
| --- | --- |
| `GET /xyz?id=<videoId>` 或 `GET /mp3?id=<videoId>` | 同步获取 MP3，保留旧接口兼容 |
| `GET /mp4?id=<videoId>` | 同步获取 MP4 视频，参数是 11 位视频 ID |
| `GET /download/<videoId>/<mp3\|mp4>/<文件名>` | 直接读取已下载的媒体文件（文件名需 URL 编码）。只允许读取 `download/` 内的 mp3/mp4/m4a/webm/opus/vtt/srt 文件，拒绝 `..`、隐藏文件、日志和越界的符号链接 |

将 `VIDEO_ID` 替换为有权下载的视频 ID；Windows PowerShell 使用 `curl.exe` 避免别名歧义：

```sh
curl --fail --show-error "http://127.0.0.1:8080/xyz?id=VIDEO_ID" --output audio.mp3
curl --fail --show-error -H "Range: bytes=0-1023" "http://127.0.0.1:8080/xyz?id=VIDEO_ID" --output audio.part
```

正常返回文件；缓存协商命中可返回 `304`，有效 Range 请求由框架处理为部分内容响应，已有路径对应文件不存在时返回 `404`。响应带 `Content-Disposition`（UTF-8 文件名）。视频 ID 缺失或格式无效返回 `400`；yt-dlp 启动失败或没有产出文件时返回 `502`。错误响应体为 JSON，例如 `{"ok":false,"error":"download produced no file","taskId":"…","exitCode":1,"detail":"ERROR: [youtube] …: This video is unavailable"}`，其中 `detail` 只包含 yt-dlp 的 `ERROR` 行。

文件保存到 `download/<videoId>/mp3/` 或 `download/<videoId>/mp4/`，已有对应格式文件会复用。MP4 优先 H.264 视频 + M4A 音频（兼容性最好，YouTube 上最高约 1080p），无则依次回退为其他 MP4 视频（可能是 AV1）、单文件 MP4、任意音视频合并为 MP4；需在 PATH 中安装 FFmpeg。同一视频的并发请求只会触发一次下载。服务日志位于 `logs/`，yt-dlp 输出位于 `yt-dlp-task-log/<taskId>/`；下载缓存没有自动清理机制。当前没有异步任务查询、进度查询或取消接口。

## 安全与公开仓库

源码可在排除运行数据并明确许可证后公开；当前示例不适合直接作为匿名公网服务：

- 未实现鉴权、限流、并发上限、任务超时及磁盘配额。
- `id` 限定为 11 位 YouTube 视频 ID；生产部署仍应做好下载目录权限隔离。
- 启用了 CORS，应收紧跨域策略；跨域配置不能替代鉴权。
- 未安装 Deno 时 yt-dlp 会警告缺少 JS 运行时，部分格式可能缺失；建议安装 Deno 并配置 `DENO_BIN_*`。
- 底层库会记录命令，代理凭据和密码参数会被打码；仍应限制日志访问。

不要提交日志、cookies、`.env`、私钥和下载内容。`.gitignore` 不会移除已跟踪文件或清理历史。

## 测试与许可证

`ParsePlayListId.java` 依赖开发者本地 JSON 文件，是手工解析示例，不是可移植的自动化验收。打包通过不等于真实视频下载通过。

server 尚无独立 LICENSE，公开发布前需要权利人明确适用许可。库模块现有 [Apache-2.0](../yt-dlp-java/LICENSE) 不应被误写为 MIT。

## 更新 yt-dlp

在服务的工作目录执行以下维护命令：

```sh
java -jar target/yt-dlp-server-1.0.0.jar --update-yt-dlp
```

也可指定可执行文件，例如 Windows：

```sh
java -Dyt.dlp.bin.win=C:/tools/yt-dlp.exe -jar target/yt-dlp-server-1.0.0.jar --update-yt-dlp
```

命令调用官方自更新器，输出更新结果后退出，不启动 HTTP 监听。成功或已是最新版本时退出码为 0，失败为非零。可执行文件缺失时先执行安装。需要 GitHub 网络访问和文件写权限；包管理器安装版本请使用原包管理器升级。更新前停止使用同一可执行文件的下载任务。

该入口使用 Java 系统属性或环境变量设置二进制路径，不加载服务启动阶段的配置文件。更新忽略 yt-dlp 配置文件，不注入 `YT_DLP_PROXY`；如需代理，使用 yt-dlp 支持的系统代理环境变量。

MP4 调用示例（将 VIDEO_ID 替换为有权下载的视频 ID）：

```sh
curl --fail --show-error "http://127.0.0.1:8080/mp4?id=VIDEO_ID" --output video.mp4
```
