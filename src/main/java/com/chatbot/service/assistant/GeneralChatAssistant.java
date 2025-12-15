package com.chatbot.service.assistant;

import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.SimpleChatResponse;
import com.chatbot.enums.AssistantType;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * General Chat Assistant - Handles general conversations
 */
@Slf4j
@Service
public class GeneralChatAssistant implements AssistantService {

    private final OpenAiChatModel chatModel;
    
    public GeneralChatAssistant(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    interface GeneralAI {
        String chat(String message);
    }

    @Override
    public AssistantType getAssistantType() {
        return AssistantType.GENERAL_CHAT;
    }

    @Override
    public CompletableFuture<SimpleChatResponse> processMessage(ChatRequest request, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                GeneralAI assistant = AiServices.builder(GeneralAI.class)
                        .chatLanguageModel(chatModel)
                        .build();

                String enhancedMessage = getSystemPrompt() + "\n\nUser: " + request.getMessage();
                String response = assistant.chat(enhancedMessage);

                return SimpleChatResponse.success(response, request.getSessionId(), getAssistantType(), null);

            } catch (Exception e) {
                System.err.println("Error processing general chat message: " + e.getMessage());
                return SimpleChatResponse.error("Failed to process your message: " + e.getMessage(), "GENERAL_CHAT_ERROR");
            }
        });
    }

    @Override
    public String getSystemPrompt() {
        return """
            You are a helpful AI assistant. You can help with:
            
            1. General conversations and questions
            2. Providing information on various topics
            3. Helping with problem-solving
            4. Offering advice and suggestions
            5. Explaining concepts and ideas
            
            You should be:
            - Friendly and conversational
            - Helpful and informative
            - Clear and concise in your responses
            - Professional but approachable
            
            If users ask about specific technical services or APIs, you can guide them to use the appropriate specialized assistant:
            - For portfolio APIs and file operations: Portfolio API Assistant
            - For PDF operations: PDF Operations Assistant
            - For coding help: Code Helper Assistant
            
            Always be helpful and try to provide the best assistance possible.
            """;
    }
}