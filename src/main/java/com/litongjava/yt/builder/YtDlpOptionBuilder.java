package com.litongjava.yt.builder;

/**
 * YtDlpOptionBuilder is a builder class for constructing command-line options
 * for yt-dlp. It uses a StringBuilder to accumulate options and parameters.
 */
public class YtDlpOptionBuilder {

  // StringBuilder to accumulate command-line options separated by two spaces.
  private final StringBuilder stringBuilder = new StringBuilder();

  // URL of the video to process.
  private String url = "";

  /**
   * Appends a command-line token and two spaces as a separator.
   *
   * @param str The token to append.
   */
  private void append(String str) {
    stringBuilder.append(str).append("  ");
  }

  private void appendOption(String option, String value) {
    append(option);
    append(value);
  }

  /**
   * Sets the video URL.
   *
   * @param urla The URL of the video.
   * @return The current instance of YtDlpOptionBuilder.
   */
  public YtDlpOptionBuilder url(String urla) {
    this.url = urla;
    return this;
  }

  /**
   * Builds the final YtDlpOption object which contains the constructed
   * command-line options and the URL.
   *
   * @return A new YtDlpOption instance.
   */
  public YtDlpOption build() {
    return new YtDlpOption(stringBuilder, url);
  }

  // General Options
  public YtDlpOptionBuilder help() {
    append("--help");
    return this;
  }

  public YtDlpOptionBuilder version() {
    append("--version");
    return this;
  }

  public YtDlpOptionBuilder update() {
    append("-U");
    return this;
  }

  public YtDlpOptionBuilder updateLong() {
    append("--update");
    return this;
  }

  public YtDlpOptionBuilder noUpdate() {
    append("--no-update");
    return this;
  }

  public YtDlpOptionBuilder updateTo(String channelAtTag) {
    appendOption("--update-to", channelAtTag);
    return this;
  }

  public YtDlpOptionBuilder ignoreErrors() {
    append("-i");
    return this;
  }

  public YtDlpOptionBuilder ignoreErrorsLong() {
    append("--ignore-errors");
    return this;
  }

  public YtDlpOptionBuilder noAbortOnError() {
    append("--no-abort-on-error");
    return this;
  }

  public YtDlpOptionBuilder abortOnError() {
    append("--abort-on-error");
    return this;
  }

  public YtDlpOptionBuilder listExtractors() {
    append("--list-extractors");
    return this;
  }

  public YtDlpOptionBuilder extractorDescriptions() {
    append("--extractor-descriptions");
    return this;
  }

  public YtDlpOptionBuilder useExtractors(String names) {
    appendOption("--use-extractors", names);
    return this;
  }

  public YtDlpOptionBuilder ies(String names) {
    appendOption("--ies", names);
    return this;
  }

  public YtDlpOptionBuilder defaultSearch(String prefix) {
    appendOption("--default-search", prefix);
    return this;
  }

  public YtDlpOptionBuilder ignoreConfig() {
    append("--ignore-config");
    return this;
  }

  public YtDlpOptionBuilder noConfig() {
    append("--no-config");
    return this;
  }

  public YtDlpOptionBuilder noConfigLocations() {
    append("--no-config-locations");
    return this;
  }

  public YtDlpOptionBuilder configLocations(String path) {
    appendOption("--config-locations", path);
    return this;
  }

  public YtDlpOptionBuilder pluginDirs(String dir) {
    appendOption("--plugin-dirs", dir);
    return this;
  }

  public YtDlpOptionBuilder noPluginDirs() {
    append("--no-plugin-dirs");
    return this;
  }

  public YtDlpOptionBuilder jsRuntimes(String runtime) {
    appendOption("--js-runtimes", runtime);
    return this;
  }

  public YtDlpOptionBuilder noJsRuntimes() {
    append("--no-js-runtimes");
    return this;
  }

  public YtDlpOptionBuilder remoteComponents(String component) {
    appendOption("--remote-components", component);
    return this;
  }

  public YtDlpOptionBuilder noRemoteComponents() {
    append("--no-remote-components");
    return this;
  }

  public YtDlpOptionBuilder flatPlaylist() {
    append("--flat-playlist");
    return this;
  }

  public YtDlpOptionBuilder noFlatPlaylist() {
    append("--no-flat-playlist");
    return this;
  }

  public YtDlpOptionBuilder liveFromStart() {
    append("--live-from-start");
    return this;
  }

  public YtDlpOptionBuilder noLiveFromStart() {
    append("--no-live-from-start");
    return this;
  }

  public YtDlpOptionBuilder waitForVideo(String minMax) {
    appendOption("--wait-for-video", minMax);
    return this;
  }

  public YtDlpOptionBuilder noWaitForVideo() {
    append("--no-wait-for-video");
    return this;
  }

  public YtDlpOptionBuilder markWatched() {
    append("--mark-watched");
    return this;
  }

  public YtDlpOptionBuilder noMarkWatched() {
    append("--no-mark-watched");
    return this;
  }

  public YtDlpOptionBuilder color(String policy) {
    appendOption("--color", policy);
    return this;
  }

  public YtDlpOptionBuilder compatOptions(String opts) {
    appendOption("--compat-options", opts);
    return this;
  }

  public YtDlpOptionBuilder alias(String aliasOptions) {
    appendOption("--alias", aliasOptions);
    return this;
  }

  public YtDlpOptionBuilder presetAlias(String preset) {
    appendOption("--preset-alias", preset);
    return this;
  }

  // Network Options
  public YtDlpOptionBuilder proxy(String proxyUrl) {
    appendOption("--proxy", proxyUrl);
    return this;
  }

  public YtDlpOptionBuilder socketTimeout(String seconds) {
    appendOption("--socket-timeout", seconds);
    return this;
  }

  public YtDlpOptionBuilder sourceAddress(String ip) {
    appendOption("--source-address", ip);
    return this;
  }

  public YtDlpOptionBuilder impersonate(String client) {
    appendOption("--impersonate", client);
    return this;
  }

  public YtDlpOptionBuilder listImpersonateTargets() {
    append("--list-impersonate-targets");
    return this;
  }

  public YtDlpOptionBuilder forceIpv4() {
    append("--force-ipv4");
    return this;
  }

  public YtDlpOptionBuilder forceIpv6() {
    append("--force-ipv6");
    return this;
  }

  public YtDlpOptionBuilder enableFileUrls() {
    append("--enable-file-urls");
    return this;
  }

  // Geo-restriction
  public YtDlpOptionBuilder geoVerificationProxy(String urlValue) {
    appendOption("--geo-verification-proxy", urlValue);
    return this;
  }

  public YtDlpOptionBuilder xff(String value) {
    appendOption("--xff", value);
    return this;
  }

  // Video Selection
  public YtDlpOptionBuilder playlistItems(String itemSpec) {
    appendOption("--playlist-items", itemSpec);
    return this;
  }

  public YtDlpOptionBuilder minFilesize(String size) {
    appendOption("--min-filesize", size);
    return this;
  }

  public YtDlpOptionBuilder maxFilesize(String size) {
    appendOption("--max-filesize", size);
    return this;
  }

  public YtDlpOptionBuilder date(String date) {
    appendOption("--date", date);
    return this;
  }

  public YtDlpOptionBuilder datebefore(String date) {
    appendOption("--datebefore", date);
    return this;
  }

  public YtDlpOptionBuilder dateafter(String date) {
    appendOption("--dateafter", date);
    return this;
  }

  public YtDlpOptionBuilder matchFilters(String filter) {
    appendOption("--match-filters", filter);
    return this;
  }

  public YtDlpOptionBuilder noMatchFilters() {
    append("--no-match-filters");
    return this;
  }

  public YtDlpOptionBuilder breakMatchFilters(String filter) {
    appendOption("--break-match-filters", filter);
    return this;
  }

  public YtDlpOptionBuilder noBreakMatchFilters() {
    append("--no-break-match-filters");
    return this;
  }

  public YtDlpOptionBuilder noPlaylist() {
    append("--no-playlist");
    return this;
  }

  public YtDlpOptionBuilder yesPlaylist() {
    append("--yes-playlist");
    return this;
  }

  public YtDlpOptionBuilder ageLimit(String years) {
    appendOption("--age-limit", years);
    return this;
  }

  public YtDlpOptionBuilder downloadArchive(String file) {
    appendOption("--download-archive", file);
    return this;
  }

  public YtDlpOptionBuilder noDownloadArchive() {
    append("--no-download-archive");
    return this;
  }

  public YtDlpOptionBuilder maxDownloads(String number) {
    appendOption("--max-downloads", number);
    return this;
  }

  public YtDlpOptionBuilder breakOnExisting() {
    append("--break-on-existing");
    return this;
  }

  public YtDlpOptionBuilder noBreakOnExisting() {
    append("--no-break-on-existing");
    return this;
  }

  public YtDlpOptionBuilder breakPerInput() {
    append("--break-per-input");
    return this;
  }

  public YtDlpOptionBuilder noBreakPerInput() {
    append("--no-break-per-input");
    return this;
  }

  public YtDlpOptionBuilder skipPlaylistAfterErrors(String count) {
    appendOption("--skip-playlist-after-errors", count);
    return this;
  }

  // Download Options
  public YtDlpOptionBuilder concurrentFragments(String n) {
    appendOption("--concurrent-fragments", n);
    return this;
  }

  public YtDlpOptionBuilder limitRate(String rate) {
    appendOption("--limit-rate", rate);
    return this;
  }

  public YtDlpOptionBuilder throttledRate(String rate) {
    appendOption("--throttled-rate", rate);
    return this;
  }

  public YtDlpOptionBuilder retries(String retries) {
    appendOption("--retries", retries);
    return this;
  }

  public YtDlpOptionBuilder fileAccessRetries(String retries) {
    appendOption("--file-access-retries", retries);
    return this;
  }

  public YtDlpOptionBuilder fragmentRetries(String retries) {
    appendOption("--fragment-retries", retries);
    return this;
  }

  public YtDlpOptionBuilder retrySleep(String expr) {
    appendOption("--retry-sleep", expr);
    return this;
  }

  public YtDlpOptionBuilder skipUnavailableFragments() {
    append("--skip-unavailable-fragments");
    return this;
  }

  public YtDlpOptionBuilder noAbortOnUnavailableFragments() {
    append("--no-abort-on-unavailable-fragments");
    return this;
  }

  public YtDlpOptionBuilder abortOnUnavailableFragments() {
    append("--abort-on-unavailable-fragments");
    return this;
  }

  public YtDlpOptionBuilder noSkipUnavailableFragments() {
    append("--no-skip-unavailable-fragments");
    return this;
  }

  public YtDlpOptionBuilder keepFragments() {
    append("--keep-fragments");
    return this;
  }

  public YtDlpOptionBuilder noKeepFragments() {
    append("--no-keep-fragments");
    return this;
  }

  public YtDlpOptionBuilder bufferSize(String size) {
    appendOption("--buffer-size", size);
    return this;
  }

  public YtDlpOptionBuilder resizeBuffer() {
    append("--resize-buffer");
    return this;
  }

  public YtDlpOptionBuilder noResizeBuffer() {
    append("--no-resize-buffer");
    return this;
  }

  public YtDlpOptionBuilder httpChunkSize(String size) {
    appendOption("--http-chunk-size", size);
    return this;
  }

  public YtDlpOptionBuilder playlistRandom() {
    append("--playlist-random");
    return this;
  }

  public YtDlpOptionBuilder lazyPlaylist() {
    append("--lazy-playlist");
    return this;
  }

  public YtDlpOptionBuilder noLazyPlaylist() {
    append("--no-lazy-playlist");
    return this;
  }

  public YtDlpOptionBuilder hlsUseMpegts() {
    append("--hls-use-mpegts");
    return this;
  }

  public YtDlpOptionBuilder noHlsUseMpegts() {
    append("--no-hls-use-mpegts");
    return this;
  }

  public YtDlpOptionBuilder downloadSections(String regex) {
    appendOption("--download-sections", regex);
    return this;
  }

  public YtDlpOptionBuilder downloader(String name) {
    appendOption("--downloader", name);
    return this;
  }

  public YtDlpOptionBuilder downloaderArgs(String nameArgs) {
    appendOption("--downloader-args", nameArgs);
    return this;
  }

  public YtDlpOptionBuilder externalDownloader(String name) {
    appendOption("--external-downloader", name);
    return this;
  }

  public YtDlpOptionBuilder externalDownloaderArgs(String nameArgs) {
    appendOption("--external-downloader-args", nameArgs);
    return this;
  }

  // Filesystem Options
  public YtDlpOptionBuilder batchFile(String file) {
    appendOption("--batch-file", file);
    return this;
  }

  public YtDlpOptionBuilder noBatchFile() {
    append("--no-batch-file");
    return this;
  }

  public YtDlpOptionBuilder paths(String path) {
    appendOption("--paths", path);
    return this;
  }

  public YtDlpOptionBuilder output(String template) {
    appendOption("--output", template);
    return this;
  }

  public YtDlpOptionBuilder outputNaPlaceholder(String text) {
    appendOption("--output-na-placeholder", text);
    return this;
  }

  public YtDlpOptionBuilder restrictFilenames() {
    append("--restrict-filenames");
    return this;
  }

  public YtDlpOptionBuilder noRestrictFilenames() {
    append("--no-restrict-filenames");
    return this;
  }

  public YtDlpOptionBuilder windowsFilenames() {
    append("--windows-filenames");
    return this;
  }

  public YtDlpOptionBuilder noWindowsFilenames() {
    append("--no-windows-filenames");
    return this;
  }

  public YtDlpOptionBuilder trimFilenames(String length) {
    appendOption("--trim-filenames", length);
    return this;
  }

  public YtDlpOptionBuilder noOverwrites() {
    append("--no-overwrites");
    return this;
  }

  public YtDlpOptionBuilder forceOverwrites() {
    append("--force-overwrites");
    return this;
  }

  public YtDlpOptionBuilder noForceOverwrites() {
    append("--no-force-overwrites");
    return this;
  }

  public YtDlpOptionBuilder continueDownload() {
    append("--continue");
    return this;
  }

  public YtDlpOptionBuilder noContinue() {
    append("--no-continue");
    return this;
  }

  public YtDlpOptionBuilder part() {
    append("--part");
    return this;
  }

  public YtDlpOptionBuilder noPart() {
    append("--no-part");
    return this;
  }

  public YtDlpOptionBuilder mtime() {
    append("--mtime");
    return this;
  }

  public YtDlpOptionBuilder noMtime() {
    append("--no-mtime");
    return this;
  }

  public YtDlpOptionBuilder writeDescription() {
    append("--write-description");
    return this;
  }

  public YtDlpOptionBuilder noWriteDescription() {
    append("--no-write-description");
    return this;
  }

  public YtDlpOptionBuilder writeInfoJson() {
    append("--write-info-json");
    return this;
  }

  public YtDlpOptionBuilder noWriteInfoJson() {
    append("--no-write-info-json");
    return this;
  }

  public YtDlpOptionBuilder writePlaylistMetafiles() {
    append("--write-playlist-metafiles");
    return this;
  }

  public YtDlpOptionBuilder noWritePlaylistMetafiles() {
    append("--no-write-playlist-metafiles");
    return this;
  }

  public YtDlpOptionBuilder cleanInfoJson() {
    append("--clean-info-json");
    return this;
  }

  public YtDlpOptionBuilder noCleanInfoJson() {
    append("--no-clean-info-json");
    return this;
  }

  public YtDlpOptionBuilder writeComments() {
    append("--write-comments");
    return this;
  }

  public YtDlpOptionBuilder getComments() {
    append("--get-comments");
    return this;
  }

  public YtDlpOptionBuilder noWriteComments() {
    append("--no-write-comments");
    return this;
  }

  public YtDlpOptionBuilder noGetComments() {
    append("--no-get-comments");
    return this;
  }

  public YtDlpOptionBuilder loadInfoJson(String file) {
    appendOption("--load-info-json", file);
    return this;
  }

  public YtDlpOptionBuilder cookies(String file) {
    appendOption("--cookies", file);
    return this;
  }

  public YtDlpOptionBuilder noCookies() {
    append("--no-cookies");
    return this;
  }

  public YtDlpOptionBuilder cookiesFromBrowser(String browser) {
    appendOption("--cookies-from-browser", browser);
    return this;
  }

  public YtDlpOptionBuilder noCookiesFromBrowser() {
    append("--no-cookies-from-browser");
    return this;
  }

  public YtDlpOptionBuilder cacheDir(String dir) {
    appendOption("--cache-dir", dir);
    return this;
  }

  public YtDlpOptionBuilder noCacheDir() {
    append("--no-cache-dir");
    return this;
  }

  public YtDlpOptionBuilder rmCacheDir() {
    append("--rm-cache-dir");
    return this;
  }

  // Thumbnail Options
  public YtDlpOptionBuilder writeThumbnail() {
    append("--write-thumbnail");
    return this;
  }

  public YtDlpOptionBuilder noWriteThumbnail() {
    append("--no-write-thumbnail");
    return this;
  }

  public YtDlpOptionBuilder writeAllThumbnails() {
    append("--write-all-thumbnails");
    return this;
  }

  public YtDlpOptionBuilder listThumbnails() {
    append("--list-thumbnails");
    return this;
  }

  // Internet Shortcut Options
  public YtDlpOptionBuilder writeLink() {
    append("--write-link");
    return this;
  }

  public YtDlpOptionBuilder writeUrlLink() {
    append("--write-url-link");
    return this;
  }

  public YtDlpOptionBuilder writeWeblocLink() {
    append("--write-webloc-link");
    return this;
  }

  public YtDlpOptionBuilder writeDesktopLink() {
    append("--write-desktop-link");
    return this;
  }

  // Verbosity and Simulation Options
  public YtDlpOptionBuilder quiet() {
    append("--quiet");
    return this;
  }

  public YtDlpOptionBuilder noQuiet() {
    append("--no-quiet");
    return this;
  }

  public YtDlpOptionBuilder noWarnings() {
    append("--no-warnings");
    return this;
  }

  public YtDlpOptionBuilder simulate() {
    append("--simulate");
    return this;
  }

  public YtDlpOptionBuilder noSimulate() {
    append("--no-simulate");
    return this;
  }

  public YtDlpOptionBuilder ignoreNoFormatsError() {
    append("--ignore-no-formats-error");
    return this;
  }

  public YtDlpOptionBuilder noIgnoreNoFormatsError() {
    append("--no-ignore-no-formats-error");
    return this;
  }

  public YtDlpOptionBuilder skipDownload() {
    append("--skip-download");
    return this;
  }

  public YtDlpOptionBuilder noDownload() {
    append("--no-download");
    return this;
  }

  public YtDlpOptionBuilder print(String template) {
    appendOption("--print", template);
    return this;
  }

  public YtDlpOptionBuilder printToFile(String templateFile) {
    appendOption("--print-to-file", templateFile);
    return this;
  }

  public YtDlpOptionBuilder printToFile(String template, String file) {
    append("--print-to-file");
    append(template);
    append(file);
    return this;
  }

  public YtDlpOptionBuilder dumpJson() {
    append("--dump-json");
    return this;
  }

  public YtDlpOptionBuilder dumpSingleJson() {
    append("--dump-single-json");
    return this;
  }

  public YtDlpOptionBuilder forceWriteArchive() {
    append("--force-write-archive");
    return this;
  }

  public YtDlpOptionBuilder forceDownloadArchive() {
    append("--force-download-archive");
    return this;
  }

  public YtDlpOptionBuilder newline() {
    append("--newline");
    return this;
  }

  public YtDlpOptionBuilder noProgress() {
    append("--no-progress");
    return this;
  }

  public YtDlpOptionBuilder progress() {
    append("--progress");
    return this;
  }

  public YtDlpOptionBuilder consoleTitle() {
    append("--console-title");
    return this;
  }

  public YtDlpOptionBuilder progressTemplate(String template) {
    appendOption("--progress-template", template);
    return this;
  }

  public YtDlpOptionBuilder progressDelta(String seconds) {
    appendOption("--progress-delta", seconds);
    return this;
  }

  public YtDlpOptionBuilder verbose() {
    append("--verbose");
    return this;
  }

  public YtDlpOptionBuilder dumpPages() {
    append("--dump-pages");
    return this;
  }

  public YtDlpOptionBuilder writePages() {
    append("--write-pages");
    return this;
  }

  public YtDlpOptionBuilder printTraffic() {
    append("--print-traffic");
    return this;
  }

  // Workarounds
  public YtDlpOptionBuilder encoding(String encoding) {
    appendOption("--encoding", encoding);
    return this;
  }

  public YtDlpOptionBuilder legacyServerConnect() {
    append("--legacy-server-connect");
    return this;
  }

  public YtDlpOptionBuilder noCheckCertificates() {
    append("--no-check-certificates");
    return this;
  }

  public YtDlpOptionBuilder preferInsecure() {
    append("--prefer-insecure");
    return this;
  }

  public YtDlpOptionBuilder addHeaders(String fieldValue) {
    appendOption("--add-headers", fieldValue);
    return this;
  }

  public YtDlpOptionBuilder bidiWorkaround() {
    append("--bidi-workaround");
    return this;
  }

  public YtDlpOptionBuilder sleepRequests(String seconds) {
    appendOption("--sleep-requests", seconds);
    return this;
  }

  public YtDlpOptionBuilder sleepInterval(String seconds) {
    appendOption("--sleep-interval", seconds);
    return this;
  }

  public YtDlpOptionBuilder minSleepInterval(String seconds) {
    appendOption("--min-sleep-interval", seconds);
    return this;
  }

  public YtDlpOptionBuilder maxSleepInterval(String seconds) {
    appendOption("--max-sleep-interval", seconds);
    return this;
  }

  public YtDlpOptionBuilder sleepSubtitles(String seconds) {
    appendOption("--sleep-subtitles", seconds);
    return this;
  }

  // Video Format Options
  public YtDlpOptionBuilder format(String format) {
    appendOption("-f", format);
    return this;
  }

  public YtDlpOptionBuilder formatLong(String format) {
    appendOption("--format", format);
    return this;
  }

  public YtDlpOptionBuilder formatSort(String sortorder) {
    appendOption("--format-sort", sortorder);
    return this;
  }

  public YtDlpOptionBuilder formatSortReset() {
    append("--format-sort-reset");
    return this;
  }

  public YtDlpOptionBuilder formatSortForce() {
    append("--format-sort-force");
    return this;
  }

  public YtDlpOptionBuilder sForce() {
    append("--S-force");
    return this;
  }

  public YtDlpOptionBuilder noFormatSortForce() {
    append("--no-format-sort-force");
    return this;
  }

  public YtDlpOptionBuilder videoMultistreams() {
    append("--video-multistreams");
    return this;
  }

  public YtDlpOptionBuilder noVideoMultistreams() {
    append("--no-video-multistreams");
    return this;
  }

  public YtDlpOptionBuilder audioMultistreams() {
    append("--audio-multistreams");
    return this;
  }

  public YtDlpOptionBuilder noAudioMultistreams() {
    append("--no-audio-multistreams");
    return this;
  }

  public YtDlpOptionBuilder preferFreeFormats() {
    append("--prefer-free-formats");
    return this;
  }

  public YtDlpOptionBuilder noPreferFreeFormats() {
    append("--no-prefer-free-formats");
    return this;
  }

  public YtDlpOptionBuilder checkFormats() {
    append("--check-formats");
    return this;
  }

  public YtDlpOptionBuilder checkAllFormats() {
    append("--check-all-formats");
    return this;
  }

  public YtDlpOptionBuilder noCheckFormats() {
    append("--no-check-formats");
    return this;
  }

  public YtDlpOptionBuilder listFormats() {
    append("-F");
    return this;
  }

  public YtDlpOptionBuilder listFormatsLong() {
    append("--list-formats");
    return this;
  }

  public YtDlpOptionBuilder mergeOutputFormat(String format) {
    appendOption("--merge-output-format", format);
    return this;
  }

  // Subtitle Options
  public YtDlpOptionBuilder writeSub() {
    append("--write-sub");
    return this;
  }

  public YtDlpOptionBuilder writeAutoSub() {
    append("--write-auto-sub");
    return this;
  }

  public YtDlpOptionBuilder subLang(String language) {
    appendOption("--sub-lang", language);
    return this;
  }

  public YtDlpOptionBuilder writeSubs() {
    append("--write-subs");
    return this;
  }

  public YtDlpOptionBuilder noWriteSubs() {
    append("--no-write-subs");
    return this;
  }

  public YtDlpOptionBuilder writeAutoSubs() {
    append("--write-auto-subs");
    return this;
  }

  public YtDlpOptionBuilder writeAutomaticSubs() {
    append("--write-automatic-subs");
    return this;
  }

  public YtDlpOptionBuilder noWriteAutoSubs() {
    append("--no-write-auto-subs");
    return this;
  }

  public YtDlpOptionBuilder noWriteAutomaticSubs() {
    append("--no-write-automatic-subs");
    return this;
  }

  public YtDlpOptionBuilder listSubs() {
    append("--list-subs");
    return this;
  }

  public YtDlpOptionBuilder subFormat(String format) {
    appendOption("--sub-format", format);
    return this;
  }

  public YtDlpOptionBuilder subLangs(String langs) {
    appendOption("--sub-langs", langs);
    return this;
  }

  // Authentication Options
  public YtDlpOptionBuilder username(String username) {
    appendOption("--username", username);
    return this;
  }

  public YtDlpOptionBuilder password(String password) {
    appendOption("--password", password);
    return this;
  }

  public YtDlpOptionBuilder twofactor(String code) {
    appendOption("--twofactor", code);
    return this;
  }

  public YtDlpOptionBuilder netrc() {
    append("--netrc");
    return this;
  }

  public YtDlpOptionBuilder netrcLocation(String path) {
    appendOption("--netrc-location", path);
    return this;
  }

  public YtDlpOptionBuilder netrcCmd(String cmd) {
    appendOption("--netrc-cmd", cmd);
    return this;
  }

  public YtDlpOptionBuilder videoPassword(String password) {
    appendOption("--video-password", password);
    return this;
  }

  public YtDlpOptionBuilder apMso(String mso) {
    appendOption("--ap-mso", mso);
    return this;
  }

  public YtDlpOptionBuilder apUsername(String username) {
    appendOption("--ap-username", username);
    return this;
  }

  public YtDlpOptionBuilder apPassword(String password) {
    appendOption("--ap-password", password);
    return this;
  }

  public YtDlpOptionBuilder apListMso() {
    append("--ap-list-mso");
    return this;
  }

  public YtDlpOptionBuilder clientCertificate(String certFile) {
    appendOption("--client-certificate", certFile);
    return this;
  }

  public YtDlpOptionBuilder clientCertificateKey(String keyFile) {
    appendOption("--client-certificate-key", keyFile);
    return this;
  }

  public YtDlpOptionBuilder clientCertificatePassword(String password) {
    appendOption("--client-certificate-password", password);
    return this;
  }

  // Post-Processing Options
  public YtDlpOptionBuilder audio() {
    append("-x");
    return this;
  }

  public YtDlpOptionBuilder extractAudio() {
    append("--extract-audio");
    return this;
  }

  public YtDlpOptionBuilder audioFormat(String format) {
    appendOption("--audio-format", format);
    return this;
  }

  public YtDlpOptionBuilder audioQuality(String quality) {
    appendOption("--audio-quality", quality);
    return this;
  }

  public YtDlpOptionBuilder remuxVideo(String format) {
    appendOption("--remux-video", format);
    return this;
  }

  public YtDlpOptionBuilder recodeVideo(String format) {
    appendOption("--recode-video", format);
    return this;
  }

  public YtDlpOptionBuilder postprocessorArgs(String args) {
    appendOption("--postprocessor-args", args);
    return this;
  }

  public YtDlpOptionBuilder ppa(String args) {
    appendOption("--ppa", args);
    return this;
  }

  public YtDlpOptionBuilder keepVideo() {
    append("--keep-video");
    return this;
  }

  public YtDlpOptionBuilder noKeepVideo() {
    append("--no-keep-video");
    return this;
  }

  public YtDlpOptionBuilder postOverwrites() {
    append("--post-overwrites");
    return this;
  }

  public YtDlpOptionBuilder noPostOverwrites() {
    append("--no-post-overwrites");
    return this;
  }

  public YtDlpOptionBuilder embedSubs() {
    append("--embed-subs");
    return this;
  }

  public YtDlpOptionBuilder noEmbedSubs() {
    append("--no-embed-subs");
    return this;
  }

  public YtDlpOptionBuilder embedThumbnail() {
    append("--embed-thumbnail");
    return this;
  }

  public YtDlpOptionBuilder noEmbedThumbnail() {
    append("--no-embed-thumbnail");
    return this;
  }

  public YtDlpOptionBuilder embedMetadata() {
    append("--embed-metadata");
    return this;
  }

  public YtDlpOptionBuilder addMetadata() {
    append("--add-metadata");
    return this;
  }

  public YtDlpOptionBuilder noEmbedMetadata() {
    append("--no-embed-metadata");
    return this;
  }

  public YtDlpOptionBuilder noAddMetadata() {
    append("--no-add-metadata");
    return this;
  }

  public YtDlpOptionBuilder embedChapters() {
    append("--embed-chapters");
    return this;
  }

  public YtDlpOptionBuilder addChapters() {
    append("--add-chapters");
    return this;
  }

  public YtDlpOptionBuilder noEmbedChapters() {
    append("--no-embed-chapters");
    return this;
  }

  public YtDlpOptionBuilder noAddChapters() {
    append("--no-add-chapters");
    return this;
  }

  public YtDlpOptionBuilder embedInfoJson() {
    append("--embed-info-json");
    return this;
  }

  public YtDlpOptionBuilder noEmbedInfoJson() {
    append("--no-embed-info-json");
    return this;
  }

  public YtDlpOptionBuilder parseMetadata(String fromTo) {
    appendOption("--parse-metadata", fromTo);
    return this;
  }

  public YtDlpOptionBuilder replaceInMetadata(String fieldsRegexReplace) {
    appendOption("--replace-in-metadata", fieldsRegexReplace);
    return this;
  }

  public YtDlpOptionBuilder xattrs() {
    append("--xattrs");
    return this;
  }

  public YtDlpOptionBuilder concatPlaylist(String policy) {
    appendOption("--concat-playlist", policy);
    return this;
  }

  public YtDlpOptionBuilder fixup(String policy) {
    appendOption("--fixup", policy);
    return this;
  }

  public YtDlpOptionBuilder ffmpegLocation(String path) {
    appendOption("--ffmpeg-location", path);
    return this;
  }

  public YtDlpOptionBuilder exec(String cmd) {
    appendOption("--exec", cmd);
    return this;
  }

  public YtDlpOptionBuilder noExec() {
    append("--no-exec");
    return this;
  }

  public YtDlpOptionBuilder convertSubs(String format) {
    appendOption("--convert-subs", format);
    return this;
  }

  public YtDlpOptionBuilder convertSubtitles(String format) {
    appendOption("--convert-subtitles", format);
    return this;
  }

  public YtDlpOptionBuilder convertThumbnails(String format) {
    appendOption("--convert-thumbnails", format);
    return this;
  }

  public YtDlpOptionBuilder splitChapters() {
    append("--split-chapters");
    return this;
  }

  public YtDlpOptionBuilder noSplitChapters() {
    append("--no-split-chapters");
    return this;
  }

  public YtDlpOptionBuilder removeChapters(String regex) {
    appendOption("--remove-chapters", regex);
    return this;
  }

  public YtDlpOptionBuilder noRemoveChapters() {
    append("--no-remove-chapters");
    return this;
  }

  public YtDlpOptionBuilder forceKeyframesAtCuts() {
    append("--force-keyframes-at-cuts");
    return this;
  }

  public YtDlpOptionBuilder noForceKeyframesAtCuts() {
    append("--no-force-keyframes-at-cuts");
    return this;
  }

  public YtDlpOptionBuilder usePostprocessor(String nameArgs) {
    appendOption("--use-postprocessor", nameArgs);
    return this;
  }

  // SponsorBlock Options
  public YtDlpOptionBuilder sponsorblockMark(String cats) {
    appendOption("--sponsorblock-mark", cats);
    return this;
  }

  public YtDlpOptionBuilder sponsorblockRemove(String cats) {
    appendOption("--sponsorblock-remove", cats);
    return this;
  }

  public YtDlpOptionBuilder sponsorblockChapterTitle(String template) {
    appendOption("--sponsorblock-chapter-title", template);
    return this;
  }

  public YtDlpOptionBuilder noSponsorblock() {
    append("--no-sponsorblock");
    return this;
  }

  public YtDlpOptionBuilder sponsorblockApi(String urlValue) {
    appendOption("--sponsorblock-api", urlValue);
    return this;
  }

  // Extractor Options
  public YtDlpOptionBuilder extractorRetries(String retries) {
    appendOption("--extractor-retries", retries);
    return this;
  }

  public YtDlpOptionBuilder allowDynamicMpd() {
    append("--allow-dynamic-mpd");
    return this;
  }

  public YtDlpOptionBuilder noIgnoreDynamicMpd() {
    append("--no-ignore-dynamic-mpd");
    return this;
  }

  public YtDlpOptionBuilder ignoreDynamicMpd() {
    append("--ignore-dynamic-mpd");
    return this;
  }

  public YtDlpOptionBuilder noAllowDynamicMpd() {
    append("--no-allow-dynamic-mpd");
    return this;
  }

  public YtDlpOptionBuilder hlsSplitDiscontinuity() {
    append("--hls-split-discontinuity");
    return this;
  }

  public YtDlpOptionBuilder noHlsSplitDiscontinuity() {
    append("--no-hls-split-discontinuity");
    return this;
  }

  public YtDlpOptionBuilder extractorArgs(String args) {
    appendOption("--extractor-args", args);
    return this;
  }

  // Short option aliases (explicit)
  public YtDlpOptionBuilder shortH() {
    append("-h");
    return this;
  }

  public YtDlpOptionBuilder shortU() {
    append("-U");
    return this;
  }

  public YtDlpOptionBuilder shorti() {
    append("-i");
    return this;
  }

  public YtDlpOptionBuilder shortI(String itemSpec) {
    appendOption("-I", itemSpec);
    return this;
  }

  public YtDlpOptionBuilder shortt(String preset) {
    appendOption("-t", preset);
    return this;
  }

  public YtDlpOptionBuilder shortq() {
    append("-q");
    return this;
  }

  public YtDlpOptionBuilder shorts() {
    append("-s");
    return this;
  }

  public YtDlpOptionBuilder shortO(String template) {
    appendOption("-O", template);
    return this;
  }

  public YtDlpOptionBuilder shortj() {
    append("-j");
    return this;
  }

  public YtDlpOptionBuilder shortJ() {
    append("-J");
    return this;
  }

  public YtDlpOptionBuilder shortv() {
    append("-v");
    return this;
  }

  public YtDlpOptionBuilder shortN(String n) {
    appendOption("-N", n);
    return this;
  }

  public YtDlpOptionBuilder shortr(String rate) {
    appendOption("-r", rate);
    return this;
  }

  public YtDlpOptionBuilder shortR(String retries) {
    appendOption("-R", retries);
    return this;
  }

  public YtDlpOptionBuilder shorta(String file) {
    appendOption("-a", file);
    return this;
  }

  public YtDlpOptionBuilder shortP(String path) {
    appendOption("-P", path);
    return this;
  }

  public YtDlpOptionBuilder shorto(String template) {
    appendOption("-o", template);
    return this;
  }

  public YtDlpOptionBuilder shortw() {
    append("-w");
    return this;
  }

  public YtDlpOptionBuilder shortc() {
    append("-c");
    return this;
  }

  public YtDlpOptionBuilder shortF() {
    append("-F");
    return this;
  }

  public YtDlpOptionBuilder shortf(String format) {
    appendOption("-f", format);
    return this;
  }

  public YtDlpOptionBuilder shortS(String sortorder) {
    appendOption("-S", sortorder);
    return this;
  }

  public YtDlpOptionBuilder shortx() {
    append("-x");
    return this;
  }

  public YtDlpOptionBuilder shortk() {
    append("-k");
    return this;
  }

  public YtDlpOptionBuilder shortu(String username) {
    appendOption("-u", username);
    return this;
  }

  public YtDlpOptionBuilder shortp(String password) {
    appendOption("-p", password);
    return this;
  }

  public YtDlpOptionBuilder short2(String code) {
    appendOption("-2", code);
    return this;
  }

  public YtDlpOptionBuilder shortn() {
    append("-n");
    return this;
  }

  public YtDlpOptionBuilder short4() {
    append("-4");
    return this;
  }

  public YtDlpOptionBuilder short6() {
    append("-6");
    return this;
  }
}
