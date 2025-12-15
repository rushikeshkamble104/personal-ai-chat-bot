package com.chatbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;

/**
 * Configuration properties for the ChatBot application
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "app.chat")
public class ChatBotProperties {

    @NotNull
    private Memory memory = new Memory();

    @NotNull
    private RateLimit rateLimit = new RateLimit();

    @NotNull
    private Security security = new Security();

    @Data
    public static class Memory {
        @Min(1)
        @Max(1000)
        private int maxMessages = 50;

        @NotBlank
        private String storePath = "./data/chat-memory.db";
    }

    @Data
    public static class RateLimit {
        @Min(1)
        @Max(1000)
        private int requestsPerMinute = 60;

        private Duration windowSize = Duration.ofMinutes(1);
    }

    @Data
    public static class Security {
        @NotBlank
        private String apiKeyHeader = "X-API-Key";

        private String[] allowedOrigins = {"http://localhost:3000", "http://localhost:8080"};
    }
}