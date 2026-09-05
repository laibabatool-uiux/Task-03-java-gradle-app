package com.task03.app;

import com.google.gson.Gson;
import io.javalin.Javalin;

import java.util.LinkedHashMap;
import java.util.Map;

public class App {

    public static void main(String[] args) {
        final GreetingService service = new GreetingService();
        final Gson gson = new Gson();
        final int port = Integer.parseInt(
                System.getenv().getOrDefault("APP_PORT", "8080"));

        Javalin app = Javalin.create().start("0.0.0.0", port);

        app.get("/", ctx -> ctx.html(
                "<h1>Task-03 &mdash; Java Application using Gradle</h1>" +
                "<p>" + service.greet("DevOps Engineer") + "</p>" +
                "<ul>" +
                "<li><a href='/health'>/health</a></li>" +
                "<li><a href='/api/greet/Ali'>/api/greet/{name}</a></li>" +
                "</ul>"));

        app.get("/health", ctx -> {
            Map<String, String> body = new LinkedHashMap<>();
            body.put("status",  service.status());
            body.put("service", "task-03-app");
            body.put("version", GreetingService.VERSION);
            ctx.contentType("application/json").result(gson.toJson(body));
        });

        app.get("/api/greet/{name}", ctx -> {
            Map<String, String> body = new LinkedHashMap<>();
            body.put("message", service.greet(ctx.pathParam("name")));
            ctx.contentType("application/json").result(gson.toJson(body));
        });

        System.out.println("Task-03 app started on port " + port);
    }
}
