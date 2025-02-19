# yt-dlp-java

**yt-dlp-java** 是 [yt-dlp](https://github.com/yt-dlp/yt-dlp) 的一个 Java 封装库，yt-dlp 是一个用于下载 YouTube 以及其他网站视频的流行命令行工具。该项目允许你使用构建器模式构造自定义下载命令，支持同步或异步（实时日志）方式执行下载任务，并能捕获诸如下载后文件名等输出信息。

## 概述

本项目提供了以下功能：

- **自动下载 yt-dlp**：如果本地不存在 yt-dlp 可执行文件，会根据操作系统自动下载相应版本。
- **命令选项的构建器模式**：使用 `YtDlpOptionBuilder` 可轻松配置目标 URL、输出模板、音频提取选项等参数。
- **同步与异步执行**：可以通过 `ProcessBroker` 同步执行命令（捕获标准输出），也可以通过 `LongProcessBroker` 异步执行，并支持事件监听实时捕获日志。
- **实时日志与事件处理**：实时捕获 yt-dlp 的日志输出（包括文件名、进度等信息）。

## 实现原理

### 下载管理

- **自动下载可执行文件**  
  类 `YtDlp` 会检查本地是否存在 yt-dlp 可执行文件；如果不存在，则根据操作系统从 GitHub 下载相应版本（Windows、Linux 或 macOS）。

- **可执行文件判定**  
  方法 `ytDlpFile()` 与 `ytDlpUrl()` 根据系统属性 `os.name` 判断正确的文件名和下载 URL。

### 命令构造

- **构建器模式**  
  `YtDlpOptionBuilder` 允许你以流式调用的方式设置各种选项，例如目标 URL、是否提取音频、音频格式（如 mp3）、列出可用格式等。

- **选项分离**  
  构建器将命令选项累积到 `StringBuilder` 中，使用两个空格作为分隔符，之后将完整命令字符串分割成数组传递给执行模块。

### 进程执行

- **同步执行**  
  `run` 方法使用 `ProcessBroker` 来执行命令，并在进程结束后捕获所有标准输出。

- **长时间运行的执行**  
  `runLong` 方法使用 `LongProcessBroker` 异步运行命令，并记录唯一任务 ID（通过 `SnowflakeId` 生成）。通过 `ProcessStreamChangeEventListener` 回调捕获实时日志输出。

### 异常处理

- **异常传播**  
  在遇到错误（如下载网络问题或进程执行错误）时，异常会被抛出或记录，以便在应用程序中进行相应的错误处理。

## 使用说明

下面提供了一个示例代码，展示了各项功能的使用方法：

```java
package com.litongjava.yt;

import com.litongjava.yt.broker.LongProcessBroker;
import com.litongjava.yt.builder.YtDlpOption;
import com.litongjava.yt.builder.YtDlpOptionBuilder;

public class Main {

  public static void main(String[] args) {
    downloadMp3();
    // listFormat();
    // test1();
    // test2();
  }

  private static void downloadMp3() {
    // 示例：下载视频为音频并转换为 mp3 格式
    YtDlpOption options = new YtDlpOptionBuilder()
        .url("https://www.youtube.com/watch?v=PnHMAVXpKg8")
        .audio()                   // 启用音频提取
        .audioFormat("mp3")        // 设置输出音频格式为 mp3
        .build();

    // 调用下载音频方法
    YtDlp.execute(options);
  }

  private static void listFormat() {
    String format = YtDlp.getAvailableFormats("https://www.youtube.com/watch?v=PnHMAVXpKg8");
    System.out.println("format: " + format);
  }

  private static void test2() {
    LongProcessBroker longProcessBroker = new LongProcessBroker("yt-dlp.exe", "https://www.youtube.com/watch?v=PnHMAVXpKg8");
    longProcessBroker.addProcessStreamChangeEventListener(
        // 将日志输出中的变化行打印到控制台
        event -> System.out.println("event.getChangedString() = " + event.getChangedString()));
    longProcessBroker.execute();
  }

  private static void test1() {
    YtDlpOptionBuilder ytDlpOptionBuilder = new YtDlpOptionBuilder();
    ytDlpOptionBuilder.url("https://www.youtube.com/watch?v=PnHMAVXpKg8")
                      .output("%(title)s.%(ext)s");
    YtDlpOption options = ytDlpOptionBuilder.build();
    YtDlp.execute(options);
  }
}
```

### 示例用例

- **下载音频 (mp3)**  
  使用 `downloadMp3()` 方法下载并转换指定 YouTube 视频为 mp3 文件。

- **列出可用格式**  
  调用 `listFormat()` 方法，获取并打印视频的所有可下载格式。

- **测试长时间运行的进程**  
  使用 `test2()` 方法执行 yt-dlp 的长时间运行模式，并实时打印日志输出。

- **自定义输出模板**  
  `test1()` 方法展示了如何使用 `output` 方法设置自定义的输出文件模板。

## License

This project is licensed under the MIT License. Feel free to use, modify, and distribute it according to the terms of the license.