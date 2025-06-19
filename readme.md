# yt-dlp-java

**yt-dlp-java** 是一个用于调用 [yt-dlp](https://github.com/yt-dlp/yt-dlp) 命令行工具的 Java 包装器。yt-dlp 本身是一个流行的工具，支持从 YouTube 以及其他视频网站下载视频。该项目利用构建者模式（Builder Pattern）构建自定义下载命令，并支持同步或异步执行下载过程，同时可以实时捕获日志输出和下载后的文件信息。

## 核心特性

- **自动下载 yt-dlp 执行文件**  
  如果本地未检测到 yt-dlp 可执行文件，系统会根据操作系统（Windows、Linux、macOS）自动从 GitHub 下载对应版本。

- **构建者模式配置命令参数**  
  通过 `YtDlpOptionBuilder`，可以以流式调用的方式配置参数，如目标 URL、输出模板、音频提取选项、字幕下载等。

- **同步与异步执行**  
  - 同步执行：利用 `ProcessBroker` 执行命令并在结束后捕获标准输出。  
  - 异步执行：利用 `LongProcessBroker` 启动长时间运行的进程，通过事件监听器实时接收日志输出。

- **实时日志和事件处理**  
  通过 `ProcessStreamChangeEventListener` 监听 yt-dlp 的实时输出，能够捕获包括下载进度、生成的文件名等信息。

## 内部实现细节

### 1. 下载管理

- **自动执行文件下载**  
  类 `YtDlp` 在执行任务前会检查本地是否存在 yt-dlp 的可执行文件。如果不存在，则调用 `ytDlpUrl()` 方法，根据当前操作系统确定下载地址，再自动下载到指定位置。

- **操作系统判断**  
  方法 `ytDlpFile()` 和 `ytDlpUrl()` 根据系统属性 `os.name` 返回正确的可执行文件名称和对应的下载 URL。

### 2. 命令构建

- **构建者模式**  
  使用 `YtDlpOptionBuilder` 来构造命令行参数。调用链中可以设置多个参数，例如：
  - 设置目标视频 URL  
  - 指定输出文件模板（例如：`downloads/ID/%(title)s.%(ext)s`）
  - 配置是否下载音频或字幕，并设置相关格式

- **参数处理**  
  构建器内部使用 `StringBuilder` 来累积命令选项，并采用自定义的分隔符（两个空格）对选项进行分割，最终转换为命令执行所需的字符串数组。

### 3. 过程执行

- **同步执行**  
  方法 `YtDlp.execute(options)` 内部调用 `ProcessBroker` 同步运行 yt-dlp 命令，等待进程结束后返回所有标准输出内容。适合用于一次性下载并捕获输出结果的场景。

- **异步执行**  
  通过 `LongProcessBroker` 可以启动一个长时间运行的 yt-dlp 进程。  
  - 为每个任务生成唯一的任务 ID（利用 `SnowflakeId` 工具）。  
  - 可以添加 `ProcessStreamChangeEventListener` 监听器，实时接收并处理输出日志，例如实时显示下载进度、文件名等信息。

### 4. 错误处理

- 在整个执行过程中，如遇到网络问题、进程执行错误等情况，都会抛出异常或记录日志，开发者可以在调用层捕获并处理这些异常，确保应用程序稳定运行。

---
## 使用
### 测试 Yt-dlp是否可用
windows,linux,macos下载地址如下
```
https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe
https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_linux
https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp
```
```
./yt-dlp.exe  -x  --audio-format  mp3  --output  "download/AMCUqgu_cTM/mp3/%(title)s.%(ext)s"  --quiet  https://www.youtube.com/watch?v=AMCUqgu_cTM
```
### 示例代码详解
```
    <dependency>
      <groupId>com.litongjava</groupId>
      <artifactId>yt-dlp-java</artifactId>
      <version>1.0.0</version>
    </dependency>
```

下面的示例代码展示了如何使用 yt-dlp-java 的各项功能。代码中提供了多个测试方法，每个方法均实现了不同的下载功能。

```java
package com.litongjava.yt;

import com.litongjava.yt.broker.LongProcessBroker;
import com.litongjava.yt.builder.YtDlpOption;
import com.litongjava.yt.builder.YtDlpOptionBuilder;
import com.litongjava.yt.utils.SnowflakeId;

public class YtDlpTest {

  public static void main(String[] args) {
    downlodSubtitle();
    // downloadMp3();
    // listFormat();
    // test1();
    // test2();
  }

  /**
   * 下载字幕方法
   *
   * 该方法主要用于下载视频的字幕文件。通过 builder 设置目标 URL、输出文件模板、字幕下载参数（例如调用 writeSub() 下载字幕）。
   * 如果视频本身不包含指定语言的字幕，可以选择使用 writeAutoSub() 下载自动生成的字幕。
   * 注意：.subLang("en") 可用于指定字幕语言，但当视频没有相应语言的字幕时，需要去除该设置。
   */
  private static void downlodSubtitle() {
    long id = SnowflakeId.id();
    String url = "https://www.youtube.com/watch?v=AMCUqgu_cTM";
    YtDlpOption options = new YtDlpOptionBuilder()
        .url(url)                                      // 设置目标视频 URL
        .output("downloads/" + id + "/%(title)s.%(ext)s") // 指定输出文件模板
        .writeSub()                                    // 启用字幕下载（也可以使用 writeAutoSub() 下载自动字幕）
        //.subLang("en")                                // 可选：指定字幕语言（注：当该语言字幕不可用时，请勿启用）
        .skipDownload()                                // 只下载字幕，不下载视频
        .build();

    // 执行 yt-dlp 下载任务，并捕获输出结果
    String result = YtDlp.execute(options);
    System.out.println("result:");
    System.out.println(result);
  }

  /**
   * 下载音频（mp3格式）方法
   *
   * 该方法用于将视频下载为音频文件，并转换成 mp3 格式。通过 builder 启用音频提取，并设置目标输出格式及文件模板。
   */
  private static void downloadMp3() {
    long id = SnowflakeId.id();
    String url = "https://www.youtube.com/watch?v=AMCUqgu_cTM";
    YtDlpOption options = new YtDlpOptionBuilder()
        .url(url)                                      // 设置目标视频 URL
        .audio()                                       // 启用音频提取
        .audioFormat("mp3")                            // 指定输出音频格式为 mp3
        .output("downloads/" + id + "/%(title)s.%(ext)s") // 指定输出文件模板
        .build();

    // 执行 yt-dlp 下载任务，并捕获输出结果
    String result = YtDlp.execute(options);
    System.out.println("result:");
    System.out.println(result);
  }

  /**
   * 列出视频可用格式的方法
   *
   * 该方法调用 yt-dlp 获取目标视频的所有可用下载格式，并将结果打印到控制台。适用于希望查看视频支持的分辨率、编码格式等信息的场景。
   */
  private static void listFormat() {
    String format = YtDlp.getAvailableFormats("https://www.youtube.com/watch?v=PnHMAVXpKg8");
    System.out.println("format: " + format);
  }

  /**
   * 异步执行示例：实时输出日志
   *
   * 该方法利用 LongProcessBroker 启动一个长时间运行的 yt-dlp 进程，同时添加日志事件监听器。
   * 当进程产生新的日志输出时，会触发事件回调，从而可以实时打印或处理日志信息。
   */
  private static void test2() {
    LongProcessBroker longProcessBroker = new LongProcessBroker("yt-dlp.exe", "https://www.youtube.com/watch?v=PnHMAVXpKg8");
    longProcessBroker.addProcessStreamChangeEventListener(
        // 当日志输出发生变化时，打印输出
        event -> System.out.println("event.getChangedString() = " + event.getChangedString()));
    longProcessBroker.execute();
  }

  /**
   * 基本下载测试方法
   *
   * 该方法展示了如何通过构建器简单地设置 URL 和输出模板，并执行 yt-dlp 下载任务。
   */
  private static void test1() {
    YtDlpOptionBuilder ytDlpOptionBuilder = new YtDlpOptionBuilder();
    ytDlpOptionBuilder.url("https://www.youtube.com/watch?v=PnHMAVXpKg8")
                      .output("%(title)s.%(ext)s");           // 设置输出模板
    YtDlpOption options = ytDlpOptionBuilder.build();
    YtDlp.execute(options);
  }
}
```

### 方法详细讲解

1. **downlodSubtitle()**  
   - **目的**：下载视频的字幕。  
   - **流程**：  
     - 生成唯一 ID，用于组织输出文件夹。  
     - 通过 `YtDlpOptionBuilder` 设置视频 URL、输出路径及字幕下载参数。  
     - 调用 `.writeSub()` 启用字幕下载；同时通过 `.skipDownload()` 指定仅下载字幕而不下载视频。  
     - 执行命令并打印返回结果。  

2. **downloadMp3()**  
   - **目的**：下载视频的音频部分，并转换为 mp3 格式。  
   - **流程**：  
     - 生成唯一 ID 用于文件管理。  
     - 设置视频 URL、启用音频提取（调用 `.audio()`）以及指定音频格式为 mp3（调用 `.audioFormat("mp3")`）。  
     - 设置输出文件模板并执行下载任务，最后输出结果。

3. **listFormat()**  
   - **目的**：获取目标视频所有可用的下载格式（如分辨率、编码格式等信息）。  
   - **流程**：  
     - 直接调用 `YtDlp.getAvailableFormats(url)` 方法传入目标视频 URL。  
     - 将返回的格式信息打印到控制台。

4. **test2()**  
   - **目的**：展示长时间运行的异步下载过程，并实时输出日志。  
   - **流程**：  
     - 使用 `LongProcessBroker` 初始化 yt-dlp 命令，并传入视频 URL。  
     - 添加 `ProcessStreamChangeEventListener` 监听器，用于捕获并打印实时输出的日志。  
     - 调用 `execute()` 方法启动异步进程。

5. **test1()**  
   - **目的**：简单展示如何使用构建者模式构建下载命令并执行下载任务。  
   - **流程**：  
     - 通过 `YtDlpOptionBuilder` 设置视频 URL 和输出文件模板。  
     - 构建 `YtDlpOption` 对象，并调用 `YtDlp.execute(options)` 执行下载命令。

---

## 使用场景

- **下载视频**：通过不设置 `.skipDownload()`，即可直接下载视频文件。  
- **下载音频**：调用 `downloadMp3()` 方法，可以将视频中的音频提取出来，并转换为 mp3 格式保存。  
- **下载字幕**：利用 `downlodSubtitle()` 方法，可以下载视频自带的字幕或者自动生成的字幕。  
- **格式列表查询**：通过 `listFormat()` 方法，可以列出视频支持的所有下载格式，方便后续定制下载选项。  
- **实时日志监控**：采用 `test2()` 方法，能够在下载过程中实时监控并处理进程日志。

---

## 许可协议

该项目遵循 MIT 许可证。您可以根据许可证条款自由使用、修改和分发该项目代码。

---

通过以上文档，开发者可以快速上手 yt-dlp-java，利用它构建适用于自己应用场景的视频、音频、字幕下载解决方案。