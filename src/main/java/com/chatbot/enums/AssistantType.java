package com.chatbot.enums;

/**
 * Enum defining different types of AI assistants
 */
public enum AssistantType {
    GENERAL_CHAT("General Chat Assistant", "general-chat"),
    PORTFOLIO_API("Portfolio API Assistant", "portfolio-api"),
    PDF_OPERATIONS("PDF Operations Assistant", "pdf-ops"),
    CODE_HELPER("Code Helper Assistant", "code-helper");

    private final String displayName;
    private final String code;

    AssistantType(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    public static AssistantType fromCode(String code) {
        for (AssistantType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return GENERAL_CHAT; // Default fallback
    }
}