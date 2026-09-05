package com.task03.app;

public class GreetingService {

    public static final String VERSION = "1.0.0";

    public String greet(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or empty");
        }
        return "Hello, " + name.trim() + "! Welcome to Task-03 (Java + Gradle + CI/CD).";
    }

    public String status() {
        return "UP";
    }
}
