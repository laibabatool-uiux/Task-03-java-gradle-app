package com.task03.app;

import com.google.gson.Gson;
import com.task03.app.data.BuildInfoService;
import com.task03.app.data.Catalog;
import com.task03.app.web.Pages;
import io.javalin.Javalin;

import java.util.LinkedHashMap;
import java.util.Map;

public class App {

    public static void main(String[] args) {
        final GreetingService greeting = new GreetingService();
        final BuildInfoService info = BuildInfoService.INSTANCE;
        final Gson gson = new Gson();
        final int port = info.port();

        Javalin app = Javalin.create().start("0.0.0.0", port);

        // ---- pages ----
        app.get("/", ctx -> ctx.html(Pages.overview()));
        app.get("/pipeline", ctx -> ctx.html(Pages.pipeline()));
        app.get("/build", ctx -> ctx.html(Pages.build()));
        app.get("/dependencies", ctx -> ctx.html(Pages.dependencies()));
        app.get("/api", ctx -> ctx.html(Pages.api()));
        app.get("/about", ctx -> ctx.html(Pages.about()));

        // ---- json ----
        app.get("/health", ctx -> {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("status", greeting.status());
            body.put("service", info.appName());
            body.put("version", info.appVersion());
            body.put("release", info.releaseId());
            body.put("uptime", info.uptime());
            ctx.contentType("application/json").result(gson.toJson(body));
        });

        app.get("/api/info", ctx ->
            ctx.contentType("application/json").result(gson.toJson(info.asMap())));

        app.get("/api/stages", ctx ->
            ctx.contentType("application/json").result(gson.toJson(Catalog.stages())));

        app.get("/api/greet/{name}", ctx -> {
            Map<String, String> body = new LinkedHashMap<>();
            body.put("message", greeting.greet(ctx.pathParam("name")));
            ctx.contentType("application/json").result(gson.toJson(body));
        });

        // ---- errors ----
        app.exception(IllegalArgumentException.class, (e, ctx) ->
            ctx.status(400).contentType("application/json")
               .result(gson.toJson(Map.of("error", e.getMessage()))));

        app.error(404, ctx -> {
            if (ctx.path().startsWith("/api") || ctx.path().equals("/health")) {
                ctx.contentType("application/json")
                   .result(gson.toJson(Map.of("error", "No endpoint for " + ctx.path())));
            } else {
                ctx.html(Pages.notFound(ctx.path()));
            }
        });

        System.out.println("Task-03 app started on port " + port
            + " (release " + info.releaseId() + ")");
    }
}
