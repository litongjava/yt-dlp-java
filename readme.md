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
## 构建
```
git clone https://github.com/litongjava/yt-dlp-java.git
mvn clean install -DskipTests -Dgpg.skip
```
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
### 示例代码
```
    <dependency>
      <groupId>com.litongjava</groupId>
      <artifactId>yt-dlp-java</artifactId>
      <version>1.0.0</version>
    </dependency>
```

下面的示例代码展示了如何使用 yt-dlp-java 的各项功能。代码中提供了多个测试方法，每个方法均实现了不同的下载功能。

```java
package com.litongjava.yt.utils;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.litongjava.tio.utils.commandline.ProcessResult;
import com.litongjava.yt.YtDlp;

public class YtDlpUtilsTest {

  @Test
  public void downlodSubtitle() {
    try {
      ProcessResult result = YtDlpUtils.downlodSubtitle("AMCUqgu_cTM", true);
      System.out.println(result.getOutput());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void downloadMp3() {
    try {
      ProcessResult downloadMp3 = YtDlpUtils.downloadMp3("AMCUqgu_cTM", true);
      File file = downloadMp3.getFile();
      System.out.println(file);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void downloadMp4() {
    ProcessResult result;
    try {
      result = YtDlpUtils.downloadMp4("AMCUqgu_cTM", true);
      if (result.getFile() != null) {
        File file = result.getFile();
        System.out.println(file.getName());
        System.out.println(file.exists());
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void listFormat() {
    try {
      ProcessResult result = YtDlp.getAvailableFormats("https://www.youtube.com/watch?v=PnHMAVXpKg8");
      System.out.println(result.getStdOut());
      System.out.println(result.getStdErr());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

}
```
---

## 许可协议

该项目遵循 MIT 许可证。您可以根据许可证条款自由使用、修改和分发该项目代码。

---

通过以上文档，开发者可以快速上手 yt-dlp-java，利用它构建适用于自己应用场景的视频、音频、字幕下载解决方案。