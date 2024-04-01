package com.chatbot.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * This dao class is used for dynamic prompt processing
 */
@Service
public interface ChatBotService {
    CompletableFuture<ResponseEntity<String>> aiChatBotHelper(String prompt);

}