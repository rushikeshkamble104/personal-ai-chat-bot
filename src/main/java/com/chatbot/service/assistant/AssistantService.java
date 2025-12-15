package com.chatbot.service.assistant;

import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.SimpleChatResponse;
import com.chatbot.enums.AssistantType;

import java.util.concurrent.CompletableFuture;

/**
 * Base interface for all assistant services
 */
public interface AssistantService {
    
    /**
     * Get the assistant type this service handles
     */
    AssistantType getAssistantType();
    
    /**
     * Process a chat request
     */
    CompletableFuture<SimpleChatResponse> processMessage(ChatRequest request, String userId);
    
    /**
     * Get the system prompt for this assistant
     */
    String getSystemPrompt();
    
    /**
     * Check if this assistant can handle file operations
     */
    default boolean supportsFileOperations() {
        return false;
    }
    
    /**
     * Check if this assistant can make API calls
     */
    default boolean supportsApiOperations() {
        return false;
    }
}