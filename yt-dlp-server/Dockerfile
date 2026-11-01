# 先在仓库根目录构建：mvn clean package -Pproduction -DskipTests
# 再在本目录构建镜像：docker build -t yt-dlp-server .
FROM debian:trixie-slim

ENV DEBIAN_FRONTEND=noninteractive
ARG DEBIAN_MIRROR=mirrors.ustc.edu.cn
# yt-dlp / Deno 版本；latest 表示构建时的最新发行版
ARG YT_DLP_VERSION=latest
ARG DENO_VERSION=latest
ARG TARGETARCH

RUN sed -i "s/deb.debian.org/${DEBIAN_MIRROR}/g" /etc/apt/sources.list.d/debian.sources \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
       ca-certificates curl unzip openjdk-21-jre-headless ffmpeg \
    && rm -rf /var/lib/apt/lists/*

# yt-dlp 独立二进制（无需 Python）；Deno 作为 YouTube 提取所需的 JS 运行时
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
    mkdir -p /opt/yt-dlp; \
    curl -fsSL --retry 3 -o /opt/yt-dlp/yt-dlp_linux "$ytbase/$ytdlp"; \
    curl -fsSL --retry 3 -o /tmp/yt-dlp-sums "$ytbase/SHA2-256SUMS"; \
    echo "$(grep " $ytdlp\$" /tmp/yt-dlp-sums | cut -d' ' -f1)  /opt/yt-dlp/yt-dlp_linux" | sha256sum -c -; \
    chmod +x /opt/yt-dlp/yt-dlp_linux; \
    curl -fsSL --retry 3 -o /tmp/deno.zip "$denobase/$deno"; \
    curl -fsSL --retry 3 -o /tmp/deno.zip.sha256sum "$denobase/$deno.sha256sum"; \
    echo "$(grep -oE '^[0-9a-fA-F]{64}' /tmp/deno.zip.sha256sum)  /tmp/deno.zip" | sha256sum -c -; \
    unzip -q /tmp/deno.zip -d /usr/local/bin; \
    rm -f /tmp/deno.zip /tmp/deno.zip.sha256sum /tmp/yt-dlp-sums; \
    /opt/yt-dlp/yt-dlp_linux --version; deno --version

RUN useradd --create-home --uid 1000 --shell /usr/sbin/nologin app \
    && mkdir -p /app/download /app/logs /app/yt-dlp-task-log \
    && chown -R app:app /app /opt/yt-dlp

COPY target/yt-dlp-server-1.0.0.jar /opt/yt-dlp-server/app.jar

USER app
# 工作目录即下载缓存、日志和 yt-dlp 任务日志所在目录
WORKDIR /app
ENV LANG=C.UTF-8 \
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
