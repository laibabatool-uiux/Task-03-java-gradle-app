package com.task03.app.data;

import java.util.List;

/** Reference data shown on the Pipeline and Dependencies pages. */
public final class Catalog {

    private Catalog() {
    }

    public record Stage(String name, String what, String command) {
    }

    public record Dep(String module, String version, String scope, String why) {
    }

    public record Endpoint(String path, String returns, String summary, boolean linkable) {
    }

    public static List<Stage> stages() {
        return List.of(
            new Stage("Checkout",
                "Pulls the commit that triggered the build from GitHub.",
                "checkout scm"),
            new Stage("Environment check",
                "Prints the JDK and Gradle versions so the log records exactly what built this release.",
                "./gradlew task03Info"),
            new Stage("Compile",
                "Turns the Java sources into class files. Fails fast on any syntax or type error.",
                "./gradlew clean compileJava"),
            new Stage("Dependency report",
                "Writes the resolved runtime dependency tree and archives it with the build.",
                "./gradlew :app:dependencies"),
            new Stage("Unit tests",
                "Runs the JUnit 5 suite. A single failing assertion stops the release here.",
                "./gradlew test"),
            new Stage("Coverage",
                "Generates the JaCoCo report so test depth is visible, not assumed.",
                "./gradlew jacocoTestReport"),
            new Stage("Package",
                "Builds the versioned distribution archive containing the app and every dependency.",
                "./gradlew distTar"),
            new Stage("Deploy",
                "Extracts the archive into a new timestamped release directory and restarts the service.",
                "sudo task-03-deploy.sh"),
            new Stage("Smoke test",
                "Polls the health endpoint until the new release answers, so a broken deploy is caught immediately.",
                "curl /health")
        );
    }

    public static List<Dep> dependencies() {
        return List.of(
            new Dep("io.javalin:javalin", "6.3.0", "implementation",
                "Serves the pages and JSON endpoints on an embedded Jetty server."),
            new Dep("com.google.code.gson:gson", "2.11.0", "implementation",
                "Converts Java objects into the JSON returned by the API."),
            new Dep("org.slf4j:slf4j-simple", "2.0.16", "runtimeOnly",
                "Logging backend. Needed to run, never referenced at compile time."),
            new Dep("org.junit.jupiter:junit-jupiter", "5.11.0", "testImplementation",
                "The test framework. Never shipped in the distribution."),
            new Dep("org.junit.platform:junit-platform-launcher", "1.11.0", "testRuntimeOnly",
                "Discovers and launches the tests when Gradle runs the suite.")
        );
    }

    public static List<Endpoint> endpoints() {
        return List.of(
            new Endpoint("/", "HTML", "Deployment overview with the live release identifier.", true),
            new Endpoint("/pipeline", "HTML", "The nine stages the code passes through on its way here.", true),
            new Endpoint("/build", "HTML", "Runtime facts read from the JVM that is serving this page.", true),
            new Endpoint("/dependencies", "HTML", "Every library the project declares, with its scope.", true),
            new Endpoint("/api", "HTML", "This page.", true),
            new Endpoint("/about", "HTML", "What the task set out to do and how each part maps to it.", true),
            new Endpoint("/health", "JSON", "Liveness check. The deploy script blocks on this before declaring success.", true),
            new Endpoint("/api/info", "JSON", "The full runtime and release record as machine-readable data.", true),
            new Endpoint("/api/greet/{name}", "JSON", "Returns a greeting. Rejects an empty name with 400.", false),
            new Endpoint("/api/stages", "JSON", "The pipeline stage list, for anything that wants to render it elsewhere.", true)
        );
    }
}
