package com.chatbot.service;

import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.SimpleChatResponse;
import com.chatbot.enums.AssistantType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for ChatBot operations
 */
public interface ChatBotService {
    
    /**
     * Process a chat message and return AI response
     * 
     * @param request Chat request
     * @param userId User identifier
     * @return AI response
     */
    CompletableFuture<SimpleChatResponse> processMessage(ChatRequest request, String userId);
    
    /**
     * Clear chat history for a user
     * 
     * @param userId User identifier
     */
    void clearHistory(String userId);
    
    /**
     * Clear chat history for a specific assistant type
     * 
     * @param userId User identifier
     * @param assistantType Assistant type
     */
    void clearHistory(String userId, AssistantType assistantType);
    
    /**
     * Get available assistant types
     * 
     * @return List of available assistants
     */
    List<AssistantType> getAvailableAssistants();
    
    /**
     * Get assistant capabilities
     * 
     * @return Map of assistant capabilities
     */
    Map<AssistantType, Map<String, Boolean>> getAssistantCapabilities();
    
    /**
     * Check if the service is healthy
     * 
     * @return true if healthy
     */
    boolean isHealthy();
}