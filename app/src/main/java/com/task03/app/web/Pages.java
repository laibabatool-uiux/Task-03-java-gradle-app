package com.task03.app.web;

import com.task03.app.data.BuildInfoService;
import com.task03.app.data.Catalog;

import static com.task03.app.web.Layout.esc;

/** Builds the body markup for each of the six pages. */
public final class Pages {

    private static final BuildInfoService INFO = BuildInfoService.INSTANCE;

    private Pages() {
    }

    public static String overview() {
        String body = """
            <section class="status">
              <p class="now"><span class="dot" aria-hidden="true"></span>Live and serving requests</p>
              <p class="release">%s</p>
              <p class="note">Release directory created by the deploy script and linked as current.</p>
            </section>

            <dl class="facts">
              <div><dt>Version</dt><dd>%s</dd></div>
              <div><dt>Uptime</dt><dd>%s</dd></div>
              <div><dt>Java</dt><dd>%s</dd></div>
              <div><dt>Heap in use</dt><dd>%s</dd></div>
            </dl>

            <h2>How this page got here</h2>
            <p>A push to the main branch starts the Jenkins pipeline. Gradle compiles the
            sources, runs the tests and packages a versioned archive. The deploy script
            unpacks that archive into a fresh timestamped directory, repoints the
            <code class="m">current</code> symlink and restarts the service. Nothing on this
            server is edited by hand.</p>

            <h2>If a release goes wrong</h2>
            <p>Old releases stay on disk. Pointing <code class="m">current</code> back at an
            earlier directory and restarting the service returns the previous version in a
            couple of seconds, with no rebuild.</p>
            """.formatted(
                esc(INFO.releaseId()),
                esc(INFO.appVersion()),
                esc(INFO.uptime()),
                esc(INFO.javaVersion()),
                esc(INFO.usedHeap()));
        return Layout.render("/", "Overview",
            "What is deployed on this server right now, and how it got here.", body);
    }

    public static String pipeline() {
        StringBuilder list = new StringBuilder("<ol class=\"seq\">");
        for (Catalog.Stage s : Catalog.stages()) {
            list.append("<li><div><h3>").append(esc(s.name())).append("</h3>")
                .append("<p>").append(esc(s.what())).append("</p>")
                .append("<code>").append(esc(s.command())).append("</code></div></li>");
        }
        list.append("</ol>");

        String body = "<p>Every commit runs the same nine stages in the same order. A failure "
            + "at any stage stops the release, so nothing reaches this server without compiling "
            + "cleanly and passing its tests first.</p>"
            + list
            + "<h2>Why the order matters</h2>"
            + "<p>Cheap checks run before expensive ones. Compilation fails in seconds; packaging "
            + "and deployment take far longer. Putting the tests ahead of packaging means a broken "
            + "change is rejected before any artifact is built for it.</p>";
        return Layout.render("/pipeline", "Pipeline",
            "The nine stages between a commit and a running service.", body);
    }

    public static String build() {
        String body = """
            <table>
              <caption>Read from the JVM currently serving this page.</caption>
              <tbody>
                <tr><th scope="row">Service</th><td class="m">%s</td></tr>
                <tr><th scope="row">Version</th><td class="m">%s</td></tr>
                <tr><th scope="row">Release</th><td class="m">%s</td></tr>
                <tr><th scope="row">Java version</th><td class="m">%s</td></tr>
                <tr><th scope="row">Java vendor</th><td class="m">%s</td></tr>
                <tr><th scope="row">Operating system</th><td class="m">%s</td></tr>
                <tr><th scope="row">Architecture</th><td class="m">%s</td></tr>
                <tr><th scope="row">Processors</th><td class="m">%d</td></tr>
                <tr><th scope="row">Maximum heap</th><td class="m">%s</td></tr>
                <tr><th scope="row">Heap in use</th><td class="m">%s</td></tr>
                <tr><th scope="row">Host</th><td class="m">%s</td></tr>
                <tr><th scope="row">Listening on</th><td class="m">port %d</td></tr>
                <tr><th scope="row">Started</th><td class="m">%s</td></tr>
                <tr><th scope="row">Uptime</th><td class="m">%s</td></tr>
              </tbody>
            </table>

            <h2>Why the build pins its own Java version</h2>
            <p>The Gradle build declares a Java 17 toolchain, so the compiler used is fixed by the
            project rather than by whatever happens to be installed on the machine running the
            build. The same sources therefore produce the same bytecode on a laptop, on this
            server, and on a CI runner.</p>

            <h2>Two runtimes, on purpose</h2>
            <p>Jenkins itself needs Java 21 and this service runs on Java 17. Both are installed,
            and each process is pointed at the one it needs. Separating the tool from the thing it
            builds is what makes that possible.</p>
            """.formatted(
                esc(INFO.appName()), esc(INFO.appVersion()), esc(INFO.releaseId()),
                esc(INFO.javaVersion()), esc(INFO.javaVendor()),
                esc(INFO.osName()), esc(INFO.osArch()), INFO.processors(),
                esc(INFO.maxHeap()), esc(INFO.usedHeap()),
                esc(INFO.hostname()), INFO.port(),
                esc(INFO.startedAtText()), esc(INFO.uptime()));
        return Layout.render("/build", "Build", "Facts about the process serving this page.", body);
    }

    public static String dependencies() {
        StringBuilder rows = new StringBuilder();
        for (Catalog.Dep d : Catalog.dependencies()) {
            rows.append("<tr><td class=\"m\">").append(esc(d.module())).append("</td>")
                .append("<td class=\"m\">").append(esc(d.version())).append("</td>")
                .append("<td class=\"scope\">").append(esc(d.scope())).append("</td>")
                .append("<td>").append(esc(d.why())).append("</td></tr>");
        }

        String body = "<p>Versions live in a single catalogue file, so upgrading a library is one "
            + "edit reviewed in one place. Lock files record the exact versions that were resolved, "
            + "which is what keeps a build from drifting between machines.</p>"
            + "<table><caption>Declared dependencies and the scope each is granted.</caption>"
            + "<thead><tr><th>Module</th><th>Version</th><th>Scope</th><th>Why it is here</th></tr></thead>"
            + "<tbody>" + rows + "</tbody></table>"
            + "<h2>What the scopes mean</h2>"
            + "<p>A scope decides where a library is visible. Anything marked "
            + "<code class=\"m\">implementation</code> is needed to compile and to run. "
            + "<code class=\"m\">runtimeOnly</code> is needed only when the program starts, so it "
            + "never clutters the compile classpath. Test scopes never reach the shipped archive at "
            + "all. Keeping the compile classpath small is what makes builds fast and dependencies "
            + "easy to reason about.</p>"
            + "<h2>Transitive dependencies</h2>"
            + "<p>These five entries pull in around twenty more, mostly Jetty and the Kotlin standard "
            + "library that Javalin is built on. The pipeline archives the resolved tree with every "
            + "build, so what actually shipped is always recorded.</p>";
        return Layout.render("/dependencies", "Dependencies",
            "Every library this project declares, and why.", body);
    }

    public static String api() {
        StringBuilder list = new StringBuilder("<div class=\"ends\">");
        for (Catalog.Endpoint e : Catalog.endpoints()) {
            list.append("<article><span class=\"verb\">GET</span>")
                .append("<span class=\"path\">").append(esc(e.path())).append("</span>")
                .append("<p>").append(esc(e.summary())).append(" Returns ")
                .append(esc(e.returns())).append(".</p>");
            if (e.linkable()) {
                list.append("<a class=\"try\" href=\"").append(esc(e.path())).append("\">Open ")
                    .append(esc(e.path())).append("</a>");
            } else {
                list.append("<a class=\"try\" href=\"/api/greet/Task-03\">Open /api/greet/Task-03</a>");
            }
            list.append("</article>");
        }
        list.append("</div>");

        String body = "<p>Six pages and four JSON endpoints. The health endpoint is the one that "
            + "matters operationally: the deploy script polls it after every restart and fails the "
            + "build if it does not answer.</p>"
            + list;
        return Layout.render("/api", "API", "Everything this service responds to.", body);
    }

    public static String about() {
        String body = """
            <p>This is the deliverable for Task-03: a Java application whose build, test and
            deployment are automated end to end. The application itself is deliberately small,
            because the subject of the task is the machinery around it.</p>

            <h2>What the task asked for</h2>
            <table>
              <thead><tr><th>Objective</th><th>Where it is met</th></tr></thead>
              <tbody>
                <tr><td>Automate Java project builds using Gradle</td>
                    <td>One command compiles, tests, reports and packages. The Gradle wrapper pins
                        the build tool version inside the repository.</td></tr>
                <tr><td>Manage dependencies efficiently</td>
                    <td>A central version catalogue, correct scopes per dependency, and lock files
                        that pin resolved versions across machines.</td></tr>
                <tr><td>Integrate CI/CD for continuous delivery</td>
                    <td>A Jenkins pipeline defined in the repository runs on every push and carries
                        a passing commit all the way to a running service.</td></tr>
                <tr><td>Streamline build and deployment</td>
                    <td>Releases unpack into timestamped directories behind a symlink, which makes
                        both deploying and rolling back a single fast operation.</td></tr>
                <tr><td>Understand core DevOps principles</td>
                    <td>Reproducible builds, fast feedback, versioned configuration, least
                        privilege, and a service that restarts itself when it dies.</td></tr>
              </tbody>
            </table>

            <h2>The part worth arguing about</h2>
            <p>Running Jenkins on the same machine as the application is convenient for a single
            task and wrong for anything real. The build competes with the service for memory, and
            a compromised pipeline owns production directly. A separate build host, or a hosted
            runner deploying over SSH, is the honest arrangement.</p>

            <h2>Running it yourself</h2>
            <p>Clone the repository, then run <code class="m">./gradlew run</code>. The wrapper
            fetches the correct Gradle version and the application starts on port 8080. No other
            setup is required.</p>
            """;
        return Layout.render("/about", "About",
            "What this project is for, and what it deliberately gets wrong.", body);
    }

    public static String notFound(String path) {
        String body = "<p>Nothing is served at <code class=\"m\">" + esc(path)
            + "</code>. The six pages of this application are listed in the left column, "
            + "and every route the service answers is documented on the "
            + "<a href=\"/api\">API page</a>.</p>";
        return Layout.render("", "Page not found",
            "That address does not match any route.", body);
    }
}
