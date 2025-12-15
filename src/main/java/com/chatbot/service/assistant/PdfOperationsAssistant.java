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
 * PDF Operations Assistant - Specialized for PDF file operations
 */
@Slf4j
@Service
public class PdfOperationsAssistant implements AssistantService {

    private final OpenAiChatModel chatModel;
    private final ApiIntegrationService apiIntegrationService;
    
    public PdfOperationsAssistant(OpenAiChatModel chatModel, ApiIntegrationService apiIntegrationService) {
        this.chatModel = chatModel;
        this.apiIntegrationService = apiIntegrationService;
    }

    interface PdfAI {
        String chat(String message);
    }

    @Override
    public AssistantType getAssistantType() {
        return AssistantType.PDF_OPERATIONS;
    }

    @Override
    public CompletableFuture<SimpleChatResponse> processMessage(ChatRequest request, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Check if this is a PDF operation request
                if (isPdfOperationRequest(request)) {
                    return handlePdfOperation(request, userId);
                }

                // Regular chat about PDF operations
                PdfAI assistant = AiServices.builder(PdfAI.class)
                        .chatLanguageModel(chatModel)
                        .build();

                String enhancedMessage = getSystemPrompt() + "\n\nUser Question: " + request.getMessage();
                String response = assistant.chat(enhancedMessage);

                return SimpleChatResponse.success(response, request.getSessionId(), getAssistantType(), null);

            } catch (Exception e) {
                System.err.println("Error processing PDF operation message: " + e.getMessage());
                return SimpleChatResponse.error("Failed to process PDF operation: " + e.getMessage(), "PDF_OPERATION_ERROR");
            }
        });
    }

    @Override
    public String getSystemPrompt() {
        return """
            You are a PDF Operations Assistant. You specialize in helping users with PDF file operations.
            
            Available PDF Operations:
            
            1. **Unlock PDF** (Remove password protection)
               - Endpoint: POST /api/pdf/unlock
               - Parameters: file (PDF), password (string)
               - Use case: Remove password from protected PDFs
            
            2. **Merge PDFs** (Combine multiple PDFs)
               - Endpoint: POST /api/pdf/merge
               - Parameters: files (array of PDFs), order (optional)
               - Use case: Combine multiple PDF files into one
            
            3. **Split PDF** (Divide PDF into parts)
               - Endpoint: POST /api/pdf/split
               - Parameters: file (PDF), pages (ranges like "1-5,10-15" or "all")
               - Use case: Extract specific pages or split into multiple files
            
            4. **Compress PDF** (Reduce file size)
               - Endpoint: POST /api/pdf/compress
               - Parameters: file (PDF), quality (low/medium/high)
               - Use case: Reduce PDF file size for easier sharing
            
            5. **PDF Info** (Get metadata)
               - Endpoint: GET /api/pdf/info
               - Parameters: file (PDF)
               - Use case: Get information about PDF (pages, size, metadata)
            
            6. **Add Watermark** (Add text/image watermark)
               - Endpoint: POST /api/pdf/watermark
               - Parameters: file (PDF), watermark_text (string), position (string)
               - Use case: Add watermark for branding or security
            
            When helping users:
            1. Ask what PDF operation they need
            2. Guide them through required parameters
            3. Help them prepare files and settings
            4. Explain the process step by step
            5. Provide tips for best results
            
            Always be specific about file requirements and parameter formats.
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

    private boolean isPdfOperationRequest(ChatRequest request) {
        return request.getFileBase64() != null || 
               (request.getParameters() != null && request.getParameters().containsKey("pdf_operation"));
    }

    private SimpleChatResponse handlePdfOperation(ChatRequest request, String userId) {
        try {
            String operation = determinePdfOperation(request);
            Map<String, Object> result = apiIntegrationService.executePdfOperation(
                operation,
                request.getParameters(),
                request.getFileBase64(),
                request.getFileName()
            );

            String[] suggestedActions = getPdfSuggestedActions(operation);
            
            return SimpleChatResponse.withApiResult(
                "PDF operation completed successfully. Your processed file is ready for download.",
                request.getSessionId(),
                getAssistantType(),
                result,
                suggestedActions
            );

        } catch (Exception e) {
            System.err.println("PDF operation failed: " + e.getMessage());
            return SimpleChatResponse.error("PDF operation failed: " + e.getMessage(), "PDF_OPERATION_ERROR");
        }
    }

    private String determinePdfOperation(ChatRequest request) {
        if (request.getOperation() != null) {
            return request.getOperation();
        }
        
        // Try to determine from parameters
        Map<String, Object> params = request.getParameters();
        if (params != null) {
            if (params.containsKey("password")) return "unlock";
            if (params.containsKey("merge_files")) return "merge";
            if (params.containsKey("split_pages")) return "split";
            if (params.containsKey("compress_quality")) return "compress";
            if (params.containsKey("watermark_text")) return "watermark";
        }
        
        return "info"; // Default to getting PDF info
    }

    private String[] getPdfSuggestedActions(String operation) {
        return switch (operation) {
            case "unlock" -> new String[]{"Merge with other PDFs", "Split pages", "Compress file", "Add watermark"};
            case "merge" -> new String[]{"Compress merged file", "Add watermark", "Split pages"};
            case "split" -> new String[]{"Compress pages", "Merge selected pages", "Add watermark"};
            case "compress" -> new String[]{"Add watermark", "Split pages", "Get file info"};
            case "watermark" -> new String[]{"Compress file", "Split pages", "Get file info"};
            default -> new String[]{"Unlock PDF", "Merge PDFs", "Split PDF", "Compress PDF"};
        };
    }
}