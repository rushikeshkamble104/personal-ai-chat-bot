package com.chatbot.controller;

import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.SimpleChatResponse;
import com.chatbot.enums.AssistantType;
import com.chatbot.service.ChatBotService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Enterprise ChatBot REST Controller
 * Provides secure endpoints for AI chat interactions
 */
@Tag(name = "ChatBot API", description = "Multi-assistant AI chatbot with portfolio API integration")
@RestController
@RequestMapping("/chat")
@SecurityRequirement(name = "basicAuth")
public class ChatBotController {

    private final ChatBotService chatBotService;
    
    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @Operation(
        summary = "Process chat message",
        description = "Send a message to the AI chatbot and receive a response from the specified assistant type"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message processed successfully",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = SimpleChatResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request format"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/message")
    @Timed(value = "chat.message.processing", description = "Time taken to process chat message")
    public CompletableFuture<ResponseEntity<SimpleChatResponse>> processMessage(
            @Parameter(description = "Chat request with message and assistant type", required = true)
            @Valid @RequestBody ChatRequest request,
            @Parameter(hidden = true) Principal principal) {
        
        String userId = principal != null ? principal.getName() : "anonymous";
        System.out.println("Processing chat message for user: " + userId);
        
        return chatBotService.processMessage(request, userId)
                .thenApply(response -> {
                    System.out.println("Chat message processed successfully");
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    System.err.println("Error processing chat message: " + throwable.getMessage());
                    return ResponseEntity.internalServerError()
                            .body(SimpleChatResponse.error(
                                    "Sorry, I encountered an error processing your request.", 
                                    "PROCESSING_ERROR"));
                });
    }

    @Operation(
        summary = "Clear chat history",
        description = "Clear chat history for the authenticated user. Optionally specify assistant type to clear specific history."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "History cleared successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @DeleteMapping("/history")
    @Timed(value = "chat.history.clear", description = "Time taken to clear chat history")
    public ResponseEntity<Void> clearHistory(
            @Parameter(hidden = true) Principal principal,
            @Parameter(description = "Assistant type to clear specific history (optional)", 
                example = "pdf-ops")
            @RequestParam(required = false) String assistantType) {
        
        if (assistantType != null) {
            AssistantType type = AssistantType.fromCode(assistantType);
            System.out.println("Clearing chat history for user and assistant");
            String userId = principal != null ? principal.getName() : "anonymous";
            chatBotService.clearHistory(userId, type);
        } else {
            System.out.println("Clearing all chat history for user");
            String userId = principal != null ? principal.getName() : "anonymous";
            chatBotService.clearHistory(userId);
        }
        
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get available assistants",
        description = "Retrieve list of available AI assistants and their capabilities"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Assistants retrieved successfully",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = 
                    "{\"assistants\":[\"GENERAL_CHAT\",\"PORTFOLIO_API\",\"PDF_OPERATIONS\",\"CODE_HELPER\"],\"capabilities\":{\"PORTFOLIO_API\":{\"supportsFileOperations\":true,\"supportsApiOperations\":true}}}"))),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/assistants")
    public ResponseEntity<Map<String, Object>> getAssistants() {
        Map<String, Object> response = Map.of(
                "assistants", chatBotService.getAvailableAssistants(),
                "capabilities", chatBotService.getAssistantCapabilities()
        );
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Health check",
        description = "Check the health status of the chatbot service"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is healthy",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = 
                    "{\"status\":\"UP\",\"assistants\":4,\"timestamp\":1703123456789}"))),
        @ApiResponse(responseCode = "503", description = "Service is unhealthy")
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        boolean isHealthy = chatBotService.isHealthy();
        Map<String, Object> health = Map.of(
                "status", isHealthy ? "UP" : "DOWN",
                "assistants", chatBotService.getAvailableAssistants().size(),
                "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(health);
    }
}