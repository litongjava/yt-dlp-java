# 先在仓库根目录构建：mvn clean package -Pproduction -DskipTests
# 再在本目录构建镜像：docker build -t yt-dlp-server .
ARG DEBIAN_MIRROR=mirrors.ustc.edu.cn

# ---------- 构建阶段：精简 JRE、精简 FFmpeg、yt-dlp、Deno ----------
FROM debian:trixie-slim AS build
ARG DEBIAN_MIRROR
# FFmpeg 发行版标签及其提交，构建时校验，防止标签被改写
ARG FFMPEG_VERSION=n8.1.3
ARG FFMPEG_COMMIT=1041abdc962f4cc4f394aa8de9dc5236c0c3b9e7
# yt-dlp / Deno 版本；latest 表示构建时的最新发行版
ARG YT_DLP_VERSION=latest
ARG DENO_VERSION=latest
ARG TARGETARCH
ENV DEBIAN_FRONTEND=noninteractive

RUN sed -i "s/deb.debian.org/${DEBIAN_MIRROR}/g" /etc/apt/sources.list.d/debian.sources \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
       ca-certificates curl unzip git build-essential pkg-config nasm libmp3lame-dev \
       openjdk-21-jdk-headless \
    && rm -rf /var/lib/apt/lists/*

# 只保留应用用到的模块（jdeps 分析结果，另加日志、XML、TLS 的 EC 算法）
RUN jlink --add-modules java.base,java.desktop,java.logging,java.management,java.naming,java.security.sasl,java.sql,java.xml,jdk.crypto.ec,jdk.management,jdk.unsupported \
      --strip-debug --no-man-pages --no-header-files --compress=zip-6 --output /opt/jre \
    && /opt/jre/bin/java -version

# yt-dlp 只用 FFmpeg 做两件事：把音视频流合并/封装为 mp4（流复制）和把音频转成 mp3。
# 发行版自带的 ffmpeg 会连带安装 Mesa/LLVM 等 GPU 库，这里只编译所需的封装格式和编解码器。
# vp9 解码器仅用于探测像素格式：没有它，VP9 流复制进 mp4 时会写出空的 vpcC，文件无法播放。
RUN set -eux; \
    git clone --depth 1 --branch "$FFMPEG_VERSION" https://github.com/FFmpeg/FFmpeg.git /src/ffmpeg; \
    cd /src/ffmpeg; \
    test "$(git rev-parse HEAD)" = "$FFMPEG_COMMIT"; \
    ./configure --prefix=/opt/ffmpeg \
      --disable-everything --disable-autodetect --disable-doc --disable-debug \
      --disable-ffplay --disable-network --disable-avdevice \
      --enable-libmp3lame \
      --enable-protocol=file,pipe \
      --enable-demuxer=mov,matroska,mp3,aac,ogg,wav,flac \
      --enable-muxer=mp4,ipod,mov,matroska,webm,mp3,ogg,opus,adts,wav,null \
      --enable-decoder=aac,aac_latm,mp3,mp3float,opus,vorbis,flac,alac,pcm_s16le,pcm_f32le,vp9 \
      --enable-encoder=libmp3lame,aac,pcm_s16le \
      --enable-parser=aac,aac_latm,mpegaudio,opus,vorbis,flac,h264,hevc,av1,vp8,vp9 \
      --enable-bsf=aac_adtstoasc,extract_extradata,h264_mp4toannexb,hevc_mp4toannexb,vp9_superframe,vp9_superframe_split,av1_frame_split \
      --enable-filter=aresample,aformat,anull,acopy,atrim,null,copy,format; \
    make -j"$(nproc)"; \
    make install; \
    strip /opt/ffmpeg/bin/ffmpeg /opt/ffmpeg/bin/ffprobe; \
    /opt/ffmpeg/bin/ffmpeg -hide_banner -version | head -1

# yt-dlp 独立二进制（无需 Python）；Deno 作为 YouTube 提取所需的 JS 运行时。均校验 SHA-256。
RUN set -eux; \
    arch="${TARGETARCH:-$(dpkg --print-architecture)}"; \
    case "$arch" in \
      amd64) ytdlp=yt-dlp_linux; deno=deno-x86_64-unknown-linux-gnu.zip ;; \
      arm64) ytdlp=yt-dlp_linux_aarch64; deno=deno-aarch64-unknown-linux-gnu.zip ;; \
      *) echo "unsupported arch: $arch" >&2; exit 1 ;; \
    esac; \
    if [ "$YT_DLP_VERSION" = latest ]; then ytbase=https://github.com/yt-dlp/yt-dlp/releases/latest/download; \
    else ytbase="https://github.com/yt-dlp/yt-dlp/releases/download/${YT_DLP_VERSION}"; fi; \
    if [ "$DENO_VERSION" = latest ]; then denobase=https://github.com/denoland/deno/releases/latest/download; \
    else denobase="https://github.com/denoland/deno/releases/download/${DENO_VERSION}"; fi; \
    mkdir -p /opt/yt-dlp /opt/deno; \
    curl -fsSL --retry 3 -o /opt/yt-dlp/yt-dlp_linux "$ytbase/$ytdlp"; \
    curl -fsSL --retry 3 -o /tmp/yt-dlp-sums "$ytbase/SHA2-256SUMS"; \
    echo "$(grep " $ytdlp\$" /tmp/yt-dlp-sums | cut -d' ' -f1)  /opt/yt-dlp/yt-dlp_linux" | sha256sum -c -; \
    chmod +x /opt/yt-dlp/yt-dlp_linux; \
    curl -fsSL --retry 3 -o /tmp/deno.zip "$denobase/$deno"; \
    curl -fsSL --retry 3 -o /tmp/deno.zip.sha256sum "$denobase/$deno.sha256sum"; \
    echo "$(grep -oE '^[0-9a-fA-F]{64}' /tmp/deno.zip.sha256sum)  /tmp/deno.zip" | sha256sum -c -; \
    unzip -q /tmp/deno.zip -d /opt/deno; \
    /opt/yt-dlp/yt-dlp_linux --version; /opt/deno/deno --version

# ---------- 运行阶段 ----------
FROM debian:trixie-slim
ARG DEBIAN_MIRROR
ENV DEBIAN_FRONTEND=noninteractive

# libmp3lame0 是 FFmpeg 的 mp3 编码库；curl 用于健康检查
RUN sed -i "s/deb.debian.org/${DEBIAN_MIRROR}/g" /etc/apt/sources.list.d/debian.sources \
    && apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates curl libmp3lame0 \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --create-home --uid 1000 --shell /usr/sbin/nologin app \
    && mkdir -p /app/download /app/logs /app/yt-dlp-task-log /opt/yt-dlp \
    && chown -R app:app /app /opt/yt-dlp

COPY --from=build /opt/jre /opt/jre
COPY --from=build /opt/ffmpeg/bin/ffmpeg /opt/ffmpeg/bin/ffprobe /usr/local/bin/
COPY --from=build /opt/deno/deno /usr/local/bin/deno
COPY --from=build --chown=app:app /opt/yt-dlp/yt-dlp_linux /opt/yt-dlp/yt-dlp_linux
COPY target/yt-dlp-server-1.0.0.jar /opt/yt-dlp-server/app.jar

USER app
# 工作目录即下载缓存、日志和 yt-dlp 任务日志所在目录
WORKDIR /app
ENV PATH=/opt/jre/bin:$PATH \
    LANG=C.UTF-8 \
    SERVER_PORT=8080 \
    YT_DLP_BIN_LINUX=/opt/yt-dlp/yt-dlp_linux \
    DENO_BIN_LINUX=/usr/local/bin/deno \
    JAVA_OPTS=""
VOLUME ["/app/download"]
EXPOSE 8080
# 不带 id 的请求立即返回 400，不会触发下载
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
    CMD test "$(curl -s -o /dev/null -w "%{http_code}" "http://127.0.0.1:${SERVER_PORT}/mp3")" = 400 || exit 1
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /opt/yt-dlp-server/app.jar \"$@\"", "--"]
