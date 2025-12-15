package com.chatbot.dto;

import com.chatbot.enums.AssistantType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "AI chatbot response containing the generated message and metadata")
public class SimpleChatResponse {
    @Schema(description = "AI generated response message", 
            example = "I can help you unlock your PDF file. Please provide the file and password.")
    private String message;
    
    @Schema(description = "Session identifier", example = "user-session-123")
    private String sessionId;
    
    @Schema(description = "Assistant type that processed the message", example = "PORTFOLIO_API")
    private AssistantType assistantType;
    
    @Schema(description = "Response timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "Whether an error occurred", example = "false")
    private boolean error;
    
    @Schema(description = "Error code if applicable", example = "PROCESSING_ERROR")
    private String errorCode;
    
    @Schema(description = "Number of tokens used in processing", example = "150")
    private Integer tokensUsed;
    
    @Schema(description = "API operation result data", 
            example = "{\"status\": \"success\", \"downloadUrl\": \"https://api.example.com/download/123\"}")
    private Map<String, Object> apiResult;
    
    @Schema(description = "Suggested follow-up actions", 
            example = "[\"Compress PDF\", \"Add Watermark\", \"Split Pages\"]")
    private String[] suggestedActions;

    public SimpleChatResponse() {}

    public SimpleChatResponse(String message, String sessionId, AssistantType assistantType, 
                             LocalDateTime timestamp, boolean error, String errorCode, 
                             Integer tokensUsed, Map<String, Object> apiResult, String[] suggestedActions) {
        this.message = message;
        this.sessionId = sessionId;
        this.assistantType = assistantType;
        this.timestamp = timestamp;
        this.error = error;
        this.errorCode = errorCode;
        this.tokensUsed = tokensUsed;
        this.apiResult = apiResult;
        this.suggestedActions = suggestedActions;
    }

    public static SimpleChatResponse success(String message, String sessionId, AssistantType assistantType, Integer tokensUsed) {
        return new SimpleChatResponse(message, sessionId, assistantType, LocalDateTime.now(), false, null, tokensUsed, null, null);
    }

    public static SimpleChatResponse error(String message, String errorCode) {
        return new SimpleChatResponse(message, null, null, LocalDateTime.now(), true, errorCode, null, null, null);
    }

    public static SimpleChatResponse withApiResult(String message, String sessionId, AssistantType assistantType, 
                                                  Map<String, Object> apiResult, String[] suggestedActions) {
        return new SimpleChatResponse(message, sessionId, assistantType, LocalDateTime.now(), false, null, null, apiResult, suggestedActions);
    }

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public AssistantType getAssistantType() { return assistantType; }
    public void setAssistantType(AssistantType assistantType) { this.assistantType = assistantType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isError() { return error; }
    public void setError(boolean error) { this.error = error; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public Integer getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(Integer tokensUsed) { this.tokensUsed = tokensUsed; }
    public Map<String, Object> getApiResult() { return apiResult; }
    public void setApiResult(Map<String, Object> apiResult) { this.apiResult = apiResult; }
    public String[] getSuggestedActions() { return suggestedActions; }
    public void setSuggestedActions(String[] suggestedActions) { this.suggestedActions = suggestedActions; }
}