package com.task03.app.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/** Collects facts about the running process and the release it was deployed from. */
public final class BuildInfoService {

    public static final BuildInfoService INSTANCE = new BuildInfoService();

    private final Instant startedAt = Instant.now();

    private BuildInfoService() {
    }

    public String appName() {
        return "task-03-app";
    }

    public String appVersion() {
        String v = BuildInfoService.class.getPackage().getImplementationVersion();
        return v != null ? v : "1.0.0";
    }

    public int port() {
        return Integer.parseInt(System.getenv().getOrDefault("APP_PORT", "8080"));
    }

    public String javaVersion() {
        return System.getProperty("java.version", "unknown");
    }

    public String javaVendor() {
        return System.getProperty("java.vendor", "unknown");
    }

    public String osName() {
        return System.getProperty("os.name", "unknown");
    }

    public String osArch() {
        return System.getProperty("os.arch", "unknown");
    }

    public int processors() {
        return Runtime.getRuntime().availableProcessors();
    }

    public String maxHeap() {
        return megabytes(Runtime.getRuntime().maxMemory());
    }

    public String usedHeap() {
        Runtime r = Runtime.getRuntime();
        return megabytes(r.totalMemory() - r.freeMemory());
    }

    public String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }

    /** The timestamped release directory the deploy script created, e.g. 20260905-114639. */
    public String releaseId() {
        String home = System.getenv("APP_HOME");
        Path dir = home != null ? Paths.get(home) : Paths.get(System.getProperty("user.dir", "."));
        try {
            Path real = dir.toRealPath();
            String name = real.getFileName() != null ? real.getFileName().toString() : "";
            if (name.matches("\\d{8}-\\d{6}")) {
                return name;
            }
            Path parent = real.getParent();
            if (parent != null && parent.getFileName() != null
                    && parent.getFileName().toString().matches("\\d{8}-\\d{6}")) {
                return parent.getFileName().toString();
            }
        } catch (Exception ignored) {
            // fall through to the development default
        }
        return "local-dev";
    }

    public String startedAtText() {
        return startedAt.toString().replace('T', ' ').substring(0, 19) + " UTC";
    }

    public String uptime() {
        Duration d = Duration.between(startedAt, Instant.now());
        long h = d.toHours();
        long m = d.toMinutesPart();
        long s = d.toSecondsPart();
        if (h > 0) {
            return h + "h " + m + "m";
        }
        if (m > 0) {
            return m + "m " + s + "s";
        }
        return s + "s";
    }

    /** Machine-readable view used by the JSON endpoints. */
    public Map<String, Object> asMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("service", appName());
        m.put("version", appVersion());
        m.put("release", releaseId());
        m.put("host", hostname());
        m.put("port", port());
        m.put("java", javaVersion());
        m.put("vendor", javaVendor());
        m.put("os", osName() + " " + osArch());
        m.put("processors", processors());
        m.put("maxHeap", maxHeap());
        m.put("usedHeap", usedHeap());
        m.put("startedAt", startedAtText());
        m.put("uptime", uptime());
        return m;
    }

    private static String megabytes(long bytes) {
        return (bytes / (1024 * 1024)) + " MB";
    }
}
