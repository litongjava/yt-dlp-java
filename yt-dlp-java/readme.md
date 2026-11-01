# yt-dlp-java

[yt-dlp](https://github.com/yt-dlp/yt-dlp) 的 Java 包装库，提供参数构建器、同步进程执行，以及 YouTube 音频、视频、字幕和播放列表工具方法。实际解析和下载由外部 yt-dlp 完成。

## 环境与安装

- JDK 21：父 POM 统一配置 `maven.compiler.release=21`。
- Maven，以及可解析的 `nexus.io:tio-utils:2.1.8`。
- FFmpeg/ffprobe：提取音频、合并音视频和部分字幕转换需要，建议加入 PATH。
- 能访问目标站点的网络。部分站点需要 cookies 或 JavaScript 运行时。

在仓库根目录执行：

```sh
mvn -pl yt-dlp-java -am clean install -DskipTests
```

其他项目引用本地安装的库：

```xml
<dependency>
  <groupId>nexus.io</groupId>
  <artifactId>yt-dlp-java</artifactId>
  <version>1.0.0</version>
</dependency>
```

## 下载 MP3

```java
import java.io.File;
import nexus.io.tio.utils.commandline.ProcessResult;
import nexus.io.yt.utils.YtDlpUtils;

public class DownloadAudio {
  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      throw new IllegalArgumentException("请传入有权下载的 YouTube 视频 ID");
    }
    ProcessResult result = YtDlpUtils.downloadMp3(args[0], false);
    String path = result.getPath();
    if (path == null || !new File(path).isFile()) {
      throw new IllegalStateException("未生成音频，请检查任务日志");
    }
    System.out.println(new File(path).getAbsolutePath());
  }
}
```

文件保存到工作目录的 `download/<videoId>/mp3/`，标题按 150 字节截断以避免文件名过长。中间文件写入同目录下的 `.tmp/`，完成后才移入，结束时清理，因此不会把未完成的文件当作结果。同一视频同一格式的并发调用会串行执行，后到的调用直接复用结果。yt-dlp 的 stdout/stderr 写入 `yt-dlp-task-log/<taskId>/`，不再混入下载目录。工具方法复用已有对应后缀文件，因此再次调用不一定下载。`quiet` 控制 yt-dlp 输出，不代表关闭库日志。

## 自定义参数

```java
import nexus.io.tio.utils.commandline.ProcessResult;
import nexus.io.yt.builder.YtDlpOptionBuilder;
import nexus.io.yt.utils.YtDlp;
import nexus.io.yt.utils.YtDlpUtils;

public class ListFormats {
  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      throw new IllegalArgumentException("请传入视频 URL");
    }
    YtDlpOptionBuilder builder = YtDlpUtils.builder().url(args[0]).listFormats();
    ProcessResult result = YtDlp.execute(builder);
    System.out.println(result.getStdOut());
    System.err.println(result.getStdErr());
  }
}
```

`YtDlpUtils.builder()` 应用代理、cookies 和 Deno 配置；直接 `new YtDlpOptionBuilder()` 不会自动应用。每个选项和值作为独立参数通过 `ProcessBuilder` 执行，不经过 shell，值中的空格原样保留，不要额外加 shell 引号。`YtDlp.execute(String)` 等字符串入口仅为兼容保留（按两个空格拆分），已标记为废弃。

## 常用 API

以下方法属于 `YtDlpUtils`：

| 方法 | 说明 |
| --- | --- |
| `downloadMp3(videoId, quiet)` | 下载 MP3，返回文件或进程结果 |
| `downloadMp4(videoId, quiet)` | 下载 MP4，优先 H.264 + M4A（兼容性最好，YouTube 上最高约 1080p），无则回退其他 MP4 视频或合并为 MP4，支持缓存复用 |
| `downlodSubtitle(videoId, quiet)` | 人工字幕，保留源码拼写；读取成功时返回字幕文本 |
| `downloadAutoSubtitle(videoId, quiet)` | 自动字幕 |
| `getAvailableFormats(url)` | 可用格式 |
| `getPlayList(url)` | 播放列表 JSON |
| `getUploadDate(videoId, quiet)` | 输出 upload_date，当前未使用 quiet 参数 |

`YtDlp.execute(builder)` 同步执行，当前执行链路没有异步任务队列、取消接口或已集成的进度事件回调。

## 配置

使用 Java 系统属性（如 `-Dyt.dlp.bin.win=C:/tools/yt-dlp.exe`）或环境变量：

| 属性 | 环境变量 | 含义 |
| --- | --- | --- |
| `yt.dlp.bin.win` | `YT_DLP_BIN_WIN` | Windows yt-dlp 路径 |
| `yt.dlp.bin.linux` | `YT_DLP_BIN_LINUX` | Linux yt-dlp 路径 |
| `yt.dlp.bin.mac` | `YT_DLP_BIN_MAC` | macOS yt-dlp 路径 |
| `yt.dlp.proxy` | `YT_DLP_PROXY` | 代理 URL |
| `yt.dlp.cookies` | `YT_DLP_COOKIES` | cookies 文件路径，不是 Cookie 请求头 |
| `deno.bin.win` | `DENO_BIN_WIN` | Windows Deno 路径 |
| `deno.bin.linux` | `DENO_BIN_LINUX` | Linux Deno 路径 |
| `deno.bin.mac` | `DENO_BIN_MAC` | macOS Deno 路径 |

可执行文件配置仅在路径存在时生效，否则回退到工作目录的 `yt-dlp.exe`、`yt-dlp_linux` 或 `yt-dlp_macos`，缺失则从 GitHub 下载最新版。当前只按操作系统选择资产，不区分 CPU 架构，不做校验和验证，也不会自动更新已有文件。生产环境建议预先安装并配置绝对路径。

代理、cookies、Deno 的自动应用取决于具体方法，并非所有方法都读取全部配置。需要统一配置时使用 `YtDlpUtils.builder()`。

## 测试与排障

不访问外网的构建器测试：

```sh
mvn -f yt-dlp-java/pom.xml test -Dtest=YtDlpOptionBuilderTest
```

`YtDlpUtilsOfflineTest` 与上述测试同样不访问外网。其他现有测试调用真实网站或本机代理，不宜直接用于离线 CI。失败时检查 FFmpeg、yt-dlp 版本、网络和 cookies，并查看 `download/` 或 `yt-dlp-task-log/` 下的日志。返回结果不保证包含文件路径，应先检查再使用。

执行的命令会写入日志，其中代理 URL 中的用户信息及 `--password` 等参数的值会被替换为 `****`。日志仍可能包含 cookies 路径等本地信息，不要提交日志和 cookies。

## 许可证

本模块使用 [Apache License 2.0](LICENSE)，不是 MIT。外部工具遵循各自许可证。

## 更新 yt-dlp

调用 `YtDlp.updateYtDlp()` 返回 `ProcessResult`。方法使用当前平台配置的可执行文件；文件缺失时先按已有安装逻辑下载，然后执行官方 `--ignore-config --update`。它更新到当前渠道最新版，已是最新版时由 yt-dlp 返回无须更新的信息。

```java
ProcessResult result = YtDlp.updateYtDlp();
if (result.getExitCode() != 0) {
  throw new IllegalStateException("更新失败: " + result.getStdErr());
}
System.out.println(result.getStdOut());
```

更新需要可访问 GitHub 和目标文件写权限。包管理器安装的版本可能拒绝自更新，应使用原包管理器升级。请在无下载任务的维护窗口调用；同步方法只串行化本 JVM 内的更新调用，不会协调其他进程或下载任务。该功能不定时自动运行，也不通过 HTTP 暴露。

`--ignore-config` 避免本机 yt-dlp 配置中的其他操作影响更新；库的 `YT_DLP_PROXY` 不注入更新命令，更新网络使用 yt-dlp 自身支持的系统代理环境变量。退出码非零表示失败，不应仅根据方法返回就宣称更新成功。
