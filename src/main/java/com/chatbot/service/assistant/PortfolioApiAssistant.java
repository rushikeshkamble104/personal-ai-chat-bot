package com.chatbot.service.assistant;

import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.SimpleChatResponse;
import com.chatbot.enums.AssistantType;
import com.chatbot.service.ApiIntegrationService;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Portfolio API Assistant - Helps users interact with portfolio APIs
 */
@Slf4j
@Service
public class PortfolioApiAssistant implements AssistantService {

    private final OpenAiChatModel chatModel;
    private final ApiIntegrationService apiIntegrationService;
    
    public PortfolioApiAssistant(OpenAiChatModel chatModel, ApiIntegrationService apiIntegrationService) {
        this.chatModel = chatModel;
        this.apiIntegrationService = apiIntegrationService;
    }

    interface PortfolioAI {
        String chat(String message);
    }

    @Override
    public AssistantType getAssistantType() {
        return AssistantType.PORTFOLIO_API;
    }

    @Override
    public CompletableFuture<SimpleChatResponse> processMessage(ChatRequest request, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Check if this is an API operation request
                if (isApiOperationRequest(request)) {
                    return handleApiOperation(request, userId);
                }

                // Regular chat with portfolio context
                PortfolioAI assistant = AiServices.builder(PortfolioAI.class)
                        .chatLanguageModel(chatModel)
                        .build();

                String enhancedMessage = enhanceMessageWithContext(request.getMessage());
                String response = assistant.chat(enhancedMessage);

                return SimpleChatResponse.success(response, request.getSessionId(), getAssistantType(), null);

            } catch (Exception e) {
                System.err.println("Error processing portfolio API message: " + e.getMessage());
                return SimpleChatResponse.error("Failed to process your request: " + e.getMessage(), "PORTFOLIO_API_ERROR");
            }
        });
    }

    @Override
    public String getSystemPrompt() {
        return """
            You are a Portfolio API Assistant. Your role is to help users understand and interact with my portfolio APIs.
            
            Available APIs:
            1. PDF Operations API:
               - POST /api/pdf/unlock - Unlock password-protected PDFs
               - POST /api/pdf/merge - Merge multiple PDF files
               - POST /api/pdf/split - Split PDF into multiple files
               - POST /api/pdf/compress - Compress PDF file size
               - GET /api/pdf/info - Get PDF metadata information
            
            2. File Conversion API:
               - POST /api/convert/image-to-pdf - Convert images to PDF
               - POST /api/convert/pdf-to-images - Convert PDF to images
               - POST /api/convert/office-to-pdf - Convert Office documents to PDF
            
            3. Document Processing API:
               - POST /api/document/extract-text - Extract text from documents
               - POST /api/document/ocr - Perform OCR on images/PDFs
               - POST /api/document/watermark - Add watermark to documents
            
            When users ask about these APIs:
            1. Explain what each API does
            2. Show required parameters and request format
            3. Provide example usage
            4. Help them prepare the correct request
            5. If they want to use an API, guide them through the process
            
            For file operations, ask users to provide:
            - File (as base64 or upload)
            - Required parameters (password for unlock, page ranges for split, etc.)
            - Output preferences
            
            Be helpful, clear, and guide users step by step through API usage.
            """;
    }

    @Override
    public boolean supportsFileOperations() {
        return true;
    }

    @Override
    public boolean supportsApiOperations() {
        return true;
    }

    private boolean isApiOperationRequest(ChatRequest request) {
        return request.getTargetApiEndpoint() != null || 
               request.getOperation() != null ||
               (request.getParameters() != null && !request.getParameters().isEmpty());
    }

    private SimpleChatResponse handleApiOperation(ChatRequest request, String userId) {
        try {
            Map<String, Object> result = apiIntegrationService.executeApiOperation(
                request.getTargetApiEndpoint(),
                request.getOperation(),
                request.getParameters(),
                request.getFileBase64(),
                request.getFileName()
            );

            String[] suggestedActions = getSuggestedActions(request.getOperation());
            
            return SimpleChatResponse.withApiResult(
                "API operation completed successfully. Check the result below.",
                request.getSessionId(),
                getAssistantType(),
                result,
                suggestedActions
            );

        } catch (Exception e) {
            System.err.println("API operation failed: " + e.getMessage());
            return SimpleChatResponse.error("API operation failed: " + e.getMessage(), "API_OPERATION_ERROR");
        }
    }

    private String enhanceMessageWithContext(String message) {
        return getSystemPrompt() + "\n\nUser Question: " + message + 
               "\n\nPlease provide a helpful response about the portfolio APIs.";
    }

    private String[] getSuggestedActions(String operation) {
        return switch (operation) {
            case "pdf-unlock" -> new String[]{"Merge PDFs", "Split PDF", "Compress PDF"};
            case "pdf-merge" -> new String[]{"Split PDF", "Compress PDF", "Add Watermark"};
            case "pdf-split" -> new String[]{"Merge PDFs", "Compress PDF", "Extract Text"};
            case "image-to-pdf" -> new String[]{"Merge PDFs", "Add Watermark", "Compress PDF"};
            default -> new String[]{"View API Documentation", "Try Another Operation"};
        };
    }
}