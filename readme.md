# yt-dlp-java

通过 Java 调用 [yt-dlp](https://github.com/yt-dlp/yt-dlp)，并提供一个基于 Tio Boot 的 YouTube MP3 下载示例服务。

| 模块 | 用途 |
| --- | --- |
| [yt-dlp-java](yt-dlp-java/readme.md) | 参数构建、进程执行、音视频与字幕下载 |
| [yt-dlp-server](yt-dlp-server/readme.md) | HTTP 下载与音频文件响应 |

## 构建

需要 JDK 21、Maven、FFmpeg/ffprobe。根目录为 Maven 聚合工程，统一管理 JDK 21 和编译器。在仓库根目录执行：

```sh
mvn clean package -Pproduction -DskipTests
```

需要能解析 `nexus.io:tio-utils:2.1.0`、`nexus.io:tio-boot:2.1.0` 及其依赖。如果无法解析，请先从对应 t-io 源码安装匹配版本，或配置提供这些依赖的 Maven 仓库。当前坐标不代表已经发布到 Maven Central。

下载测试会访问外网，因此以上命令跳过测试。自动安装 yt-dlp 需要访问 GitHub Releases，实际下载还需能访问目标站点。

## 公开源码

公开前排除日志、cookies、私钥和本地配置，并明确 server 许可证。当前服务缺少鉴权、输入校验和资源限制，不适合直接开放公网。见 [公开仓库检查记录](yt-dlp-server/SECURITY_REVIEW.md)。

库现有许可证为 [Apache-2.0](yt-dlp-java/LICENSE)，server 尚无独立许可证声明。yt-dlp、FFmpeg 和其他依赖分别遵循各自许可证。仅下载和分发你有权处理的内容。

## 常用构建命令

```sh
# 安装父 POM 和两个模块
mvn clean install -Pproduction -DskipTests
# 仅构建 server 及其依赖模块
mvn -pl yt-dlp-server -am package -Pproduction -DskipTests
# 离线行为测试（不访问视频站点）
mvn test "-Dtest=YtDlpOptionBuilderTest,YtDlpUpdateTest" "-Dsurefire.failIfNoSpecifiedTests=false"
```

## 更新 yt-dlp

```sh
cd yt-dlp-server
java -jar target/yt-dlp-server-1.0.0.jar --update-yt-dlp
```

更新命令执行后退出，不启动 HTTP 服务。完整行为与配置见 [server 文档](yt-dlp-server/readme.md#更新-yt-dlp)。
