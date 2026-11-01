# yt-dlp-server

基于 Tio Boot 和 [yt-dlp-java](../yt-dlp-java/readme.md) 的 YouTube MP3/MP4 下载示例。请求传入视频 ID，同步下载或复用本地文件，再返回音频或视频，支持缓存协商和 Range 请求。

## 构建与启动

需要 JDK 21（代码使用虚拟线程）、Maven、FFmpeg/ffprobe，以及可访问目标网站的网络。Maven 需能解析 `nexus.io:tio-boot:2.1.0` 及相关依赖。

从仓库根目录执行：

```sh
mvn clean package -Pproduction -DskipTests
cd yt-dlp-server
java -Dserver.port=8080 -jar target/yt-dlp-server-1.0.0.jar
```

`production` profile 生成包含依赖的可执行 JAR；默认 profile 不执行 repackage。`assembly` profile 引用的描述文件未提供，不作为推荐入口。

框架默认端口为 80，示例显式使用 8080。端口配置不限制监听地址，请在防火墙保护的开发环境运行。工作目录必须可写，用于保存下载文件、日志和 yt-dlp。

通过 `YT_DLP_BIN_WIN`、`YT_DLP_BIN_LINUX`、`YT_DLP_BIN_MAC` 指向现有 yt-dlp，`YT_DLP_PROXY` 配置代理，`YT_DLP_COOKIES` 指向外部 cookies 文件，`DENO_BIN_*` 配置 Deno。详见 [库配置](../yt-dlp-java/readme.md#配置)。

## HTTP 接口

| 路径 | 行为 |
| --- | --- |
| `GET /xyz?id=<videoId>` 或 `GET /mp3?id=<videoId>` | 同步获取 MP3，保留旧接口兼容 |
| `GET /mp4?id=<videoId>` | 同步获取 MP4 视频，参数是 11 位视频 ID |
| `/download/**` | 框架文件处理路由，部署前需审查可读范围 |

将 `VIDEO_ID` 替换为有权下载的视频 ID；Windows PowerShell 使用 `curl.exe` 避免别名歧义：

```sh
curl --fail --show-error "http://127.0.0.1:8080/xyz?id=VIDEO_ID" --output audio.mp3
curl --fail --show-error -H "Range: bytes=0-1023" "http://127.0.0.1:8080/xyz?id=VIDEO_ID" --output audio.part
```

正常返回文件；缓存协商命中可返回 `304`，有效 Range 请求由框架处理为部分内容响应，已有路径对应文件不存在时返回 `404`。视频 ID 缺失或格式无效返回 `400`，下载结果没有文件路径返回 `502`；进程异常仍由框架处理，不保证统一 JSON 错误协议。

文件保存到 `download/<videoId>/mp3/` 或 `download/<videoId>/mp4/`，已有对应格式文件会复用。MP4 选择 MP4 视频与 M4A 音频并使用 FFmpeg 合并，回退到站点提供的单文件 MP4；需在 PATH 中安装 FFmpeg。日志位于 `logs/` 和下载相关目录；下载缓存没有自动清理机制。当前没有异步任务查询、进度查询或取消接口。

## 安全与公开仓库

源码可在排除运行数据并明确许可证后公开；当前示例不适合直接作为匿名公网服务：

- 未实现鉴权、限流、并发上限、任务超时及磁盘配额。
- `id` 限定为 11 位 YouTube 视频 ID；生产部署仍应做好下载目录权限隔离。
- 启用了 CORS，应收紧跨域策略；跨域配置不能替代鉴权。
- `/download/**` 应限制可读目录，排除 cookies、日志和私有文件。
- 底层库记录完整命令，代理凭据或密码参数可能进入日志。

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
