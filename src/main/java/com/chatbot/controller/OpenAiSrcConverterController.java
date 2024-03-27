package com.chatbot.controller;

import com.chatbot.service.ChatBotService;
import com.chatbot.service.serviceImpl.ChatBotServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static com.chatbot.utility.ApiRoute.BASE_POINT;

/**
 *  open ai src controller
 *
 */
@Slf4j
@RestController
@RequestMapping(BASE_POINT)
public class OpenAiSrcConverterController {
    public static final String OPENAPI_PERSONAL_CHAT_BOT = "/openapi/personal/chat-bot";
    String className = getClass().getSimpleName();

    @Autowired
    ChatBotService chatBotService;

    /**
     * This API used to give dynamic prompt
     * @param prompt
     * @return
     */
    @PostMapping(OPENAPI_PERSONAL_CHAT_BOT)
    public CompletableFuture<ResponseEntity<String>> openAIChatBot (@RequestBody String prompt) {
        final String methodName = "openAPIResponse :: ";
        log.info(className,methodName,"start()");
        CompletableFuture<ResponseEntity<String>> responseEntity;
        responseEntity = chatBotService.aiChatBotHelper(prompt);
        log.info(className,methodName,"end()");
        return responseEntity;
    }



}
