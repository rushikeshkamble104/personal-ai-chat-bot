package com.chatbot.dto;

import com.chatbot.enums.AssistantType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for chat messages
 */
@Schema(description = "Chat request containing user message and assistant configuration")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    @Schema(description = "User message to send to the AI assistant", 
            example = "I need help unlocking a PDF file", required = true)
    @NotBlank(message = "Message cannot be empty")
    @Size(max = 4000, message = "Message cannot exceed 4000 characters")
    private String message;

    @Schema(description = "Session identifier for conversation continuity", 
            example = "user-session-123")
    @Size(max = 100, message = "Session ID cannot exceed 100 characters")
    private String sessionId;

    @Schema(description = "Type of AI assistant to use", 
            example = "PORTFOLIO_API", defaultValue = "GENERAL_CHAT")
    private AssistantType assistantType = AssistantType.GENERAL_CHAT;
    
    @Schema(description = "Whether to include conversation context", defaultValue = "true")
    private boolean includeContext = true;
    
    @Schema(description = "Additional parameters for API operations", 
            example = "{\"password\": \"secret123\", \"quality\": \"medium\"}")
    private Map<String, Object> parameters;
    
    @Schema(description = "Name of the uploaded file", example = "document.pdf")
    private String fileName;
    
    @Schema(description = "MIME type of the file", example = "application/pdf")
    private String fileType;
    
    @Schema(description = "Base64 encoded file content for processing")
    private String fileBase64;
    
    @Schema(description = "Target API endpoint for portfolio operations", 
            example = "/api/pdf/unlock")
    private String targetApiEndpoint;
    
    @Schema(description = "Operation to perform", 
            example = "unlock", allowableValues = {"unlock", "merge", "split", "compress", "watermark"})
    private String operation;
    
    public String getMessage() { return message; }
    public String getSessionId() { return sessionId; }
    public AssistantType getAssistantType() { return assistantType; }
    public Map<String, Object> getParameters() { return parameters; }
    public String getFileName() { return fileName; }
    public String getFileBase64() { return fileBase64; }
    public String getTargetApiEndpoint() { return targetApiEndpoint; }
    public String getOperation() { return operation; }
}