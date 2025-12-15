package com.chatbot.service;

import com.chatbot.enums.AssistantType;
import com.chatbot.service.assistant.AssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory service for managing different assistant types
 */
@Service
public class AssistantFactory {

    private final List<AssistantService> assistantServices;
    
    public AssistantFactory(List<AssistantService> assistantServices) {
        this.assistantServices = assistantServices;
    }
    private Map<AssistantType, AssistantService> assistantMap;

    /**
     * Get assistant service by type
     */
    public AssistantService getAssistant(AssistantType type) {
        if (assistantMap == null) {
            assistantMap = assistantServices.stream()
                    .collect(Collectors.toMap(
                            AssistantService::getAssistantType,
                            Function.identity()
                    ));
        }
        
        return assistantMap.getOrDefault(type, assistantMap.get(AssistantType.GENERAL_CHAT));
    }

    /**
     * Get all available assistant types
     */
    public List<AssistantType> getAvailableAssistants() {
        return assistantServices.stream()
                .map(AssistantService::getAssistantType)
                .toList();
    }

    /**
     * Get assistant capabilities
     */
    public Map<AssistantType, Map<String, Boolean>> getAssistantCapabilities() {
        return assistantServices.stream()
                .collect(Collectors.toMap(
                        AssistantService::getAssistantType,
                        service -> Map.of(
                                "supportsFileOperations", service.supportsFileOperations(),
                                "supportsApiOperations", service.supportsApiOperations()
                        )
                ));
    }
}