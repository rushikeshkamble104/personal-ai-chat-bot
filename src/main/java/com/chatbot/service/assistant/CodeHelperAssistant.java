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

@Slf4j
@Service
public class CodeHelperAssistant implements AssistantService {

    private final OpenAiChatModel chatModel;
    
    public CodeHelperAssistant(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    interface CodeAI {
        String chat(String message);
    }

    @Override
    public AssistantType getAssistantType() {
        return AssistantType.CODE_HELPER;
    }

    @Override
    public CompletableFuture<SimpleChatResponse> processMessage(ChatRequest request, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CodeAI assistant = AiServices.builder(CodeAI.class)
                        .chatLanguageModel(chatModel)
                        .build();

                String enhancedMessage = getSystemPrompt() + "\n\nUser: " + request.getMessage();
                String response = assistant.chat(enhancedMessage);

                return SimpleChatResponse.success(response, request.getSessionId(), getAssistantType(), null);

            } catch (Exception e) {
                System.err.println("Error processing code helper message: " + e.getMessage());
                return SimpleChatResponse.error("Failed to process coding question: " + e.getMessage(), "CODE_HELPER_ERROR");
            }
        });
    }

    @Override
    public String getSystemPrompt() {
        return """
            You are a Code Helper Assistant specializing in programming and software development.
            
            You can help with:
            - Code review and debugging
            - Programming best practices
            - Algorithm explanations
            - Framework and library guidance
            - Architecture recommendations
            - Performance optimization
            - Testing strategies
            
            Provide clear, practical coding advice with examples when helpful.
            """;
    }
}