package com.chatbot.service.serviceImpl;

import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.SimpleChatResponse;
import com.chatbot.enums.AssistantType;
import com.chatbot.service.AssistantFactory;
import com.chatbot.service.ChatBotService;
import com.chatbot.service.assistant.AssistantService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enterprise ChatBot service implementation using assistant factory pattern
 */
@Slf4j
@Service
public class ChatBotServiceImpl implements ChatBotService {

    private final AssistantFactory assistantFactory;
    private final Counter messageCounter;
    private final Counter errorCounter;
    private final Map<String, Map<AssistantType, Object>> userSessions = new ConcurrentHashMap<>();
    
    public ChatBotServiceImpl(AssistantFactory assistantFactory, MeterRegistry meterRegistry) {
        this.assistantFactory = assistantFactory;
        this.messageCounter = Counter.builder("chatbot.messages.total")
                .description("Total number of chat messages processed")
                .register(meterRegistry);
        this.errorCounter = Counter.builder("chatbot.errors.total")
                .description("Total number of errors")
                .register(meterRegistry);
    }


    @EventListener(ApplicationReadyEvent.class)
    public void handleEvent() {
        System.out.println("ChatBot service initialized successfully");
    }

    @Override
    public CompletableFuture<SimpleChatResponse> processMessage(ChatRequest request, String userId) {
        messageCounter.increment();
        
        try {
            AssistantType assistantType = request.getAssistantType() != null ? 
                request.getAssistantType() : AssistantType.GENERAL_CHAT;
            
            AssistantService assistant = assistantFactory.getAssistant(assistantType);
            
            System.out.println("Processing message for user with assistant type: " + assistantType);
            
            return assistant.processMessage(request, userId)
                    .exceptionally(throwable -> {
                        errorCounter.increment();
                        System.err.println("Error processing message: " + throwable.getMessage());
                        return SimpleChatResponse.error(
                                "Failed to process message: " + throwable.getMessage(), 
                                "PROCESSING_ERROR"
                        );
                    });
                    
        } catch (Exception e) {
            errorCounter.increment();
            System.err.println("Error selecting assistant: " + e.getMessage());
            return CompletableFuture.completedFuture(
                SimpleChatResponse.error("Failed to process message: " + e.getMessage(), "ASSISTANT_ERROR")
            );
        }
    }
    
    @Override
    public void clearHistory(String userId) {
        userSessions.remove(userId);
        System.out.println("Cleared all chat history for user");
    }
    
    @Override
    public void clearHistory(String userId, AssistantType assistantType) {
        Map<AssistantType, Object> userSession = userSessions.get(userId);
        if (userSession != null) {
            userSession.remove(assistantType);
            System.out.println("Cleared chat history for user and assistant");
        }
    }
    
    @Override
    public List<AssistantType> getAvailableAssistants() {
        return assistantFactory.getAvailableAssistants();
    }
    
    @Override
    public Map<AssistantType, Map<String, Boolean>> getAssistantCapabilities() {
        return assistantFactory.getAssistantCapabilities();
    }
    
    @Override
    public boolean isHealthy() {
        try {
            List<AssistantType> assistants = assistantFactory.getAvailableAssistants();
            return !assistants.isEmpty();
        } catch (Exception e) {
            System.err.println("Health check failed: " + e.getMessage());
            return false;
        }
    }
    





}