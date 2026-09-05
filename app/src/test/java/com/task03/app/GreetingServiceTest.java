package com.task03.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GreetingServiceTest {

    private final GreetingService service = new GreetingService();

    @Test
    @DisplayName("greet() returns a personalised message")
    void greetReturnsMessage() {
        String result = service.greet("Ali");
        assertTrue(result.startsWith("Hello, Ali!"));
        assertTrue(result.contains("Task-03"));
    }

    @Test
    @DisplayName("greet() trims surrounding whitespace")
    void greetTrimsInput() {
        assertTrue(service.greet("  Sara  ").startsWith("Hello, Sara!"));
    }

    @Test
    @DisplayName("greet() rejects null and blank input")
    void greetRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> service.greet(null));
        assertThrows(IllegalArgumentException.class, () -> service.greet("   "));
    }

    @Test
    @DisplayName("status() reports UP")
    void statusIsUp() {
        assertEquals("UP", service.status());
    }
}
