package com.chatbot.service.serviceImpl;

import com.chatbot.service.ChatBotService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;
import static org.mapdb.Serializer.STRING;

/**
 * This dao class is used for dynamic prompt processing
 */
@Slf4j
@Component
public class ChatBotServiceImpl implements ChatBotService {
    interface Assistant {

        String chat(String message);
    }

    ChatMemory chatMemory = MessageWindowChatMemory.builder()
            .maxMessages(50)
            .chatMemoryStore(new PersistentChatMemoryStore())
            .build();

    OpenAiChatModel model = OpenAiChatModel.builder()
//            .baseUrl("http://langchain4j.dev/demo/openai/v1")
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName("gpt-4o-mini")
            .build();

    Assistant assistant = AiServices.builder(Assistant.class)
            .chatLanguageModel(model)
            .chatMemory(chatMemory)
            .build();

    @EventListener(ApplicationReadyEvent.class)
    public ResponseEntity<String>  handleEvent() {
        String readPromptFromFile = readPromptFromFile("you_are_a_chat_bot.txt");
        String chat = assistant.chat(readPromptFromFile);
        log.info(chat);
        return ResponseEntity.ok(chat);
    }

    /**
     * read prompt from file
     *
     * @param promptFilePath promptFilePath
     * @return {@link String}
     * @see String
     */
    public static String readPromptFromFile(String promptFilePath) {
        StringBuilder promptBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(promptFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                promptBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            log.error("Exception occured", e);
        }

        return promptBuilder.toString();
    }

    /**
     * This method is used to generate dynamic prompt
     *
     * @param prompt
     * @return
     */
    public CompletableFuture<ResponseEntity<String>> aiChatBotHelper(String prompt) {

        CompletableFuture completableFuture = new CompletableFuture();
        return completableFuture.supplyAsync(() -> {
            try {

                String generatedResponse = assistant.chat(prompt);
//                String rearrangedResponse = rearrangedResponse(generatedResponse);
                return ResponseEntity.ok(generatedResponse);
            } catch (Exception e) {
                log.error("exception occured", e);
                return ResponseEntity.badRequest().build();
            }
        });
    }


    /**
     * read file as string
     *
     * @param codeFile codeFile
     * @return {@link String}
     * @throws IOException java.io. i o exception
     * @see String
     */
    public static String readFileAsString(MultipartFile codeFile) throws IOException {
        StringBuilder contentString = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(codeFile.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            contentString.append(line);
        }
        bufferedReader.close();
        return contentString.toString();
    }

    /**
     * rearranged code
     *
     * @param code code
     * @return {@link String}
     * @see String
     */
    public static String rearrangedResponse(String code) {
        String[] lines = code.split("\n");
        List<String> codeLines = new ArrayList<>();
        Collections.addAll(codeLines, lines);
        Collections.sort(codeLines);
        StringBuilder rearrangedCode = new StringBuilder();
        for (String line : codeLines) {
            rearrangedCode.append(line).append("\n");
        }
        return rearrangedCode.toString();


    }

    static class PersistentChatMemoryStore implements ChatMemoryStore {

        private final DB db = DBMaker.fileDB("chat-memory.db").transactionEnable().make();
        private final Map<String, String> map = db.hashMap("messages", STRING, STRING).createOrOpen();

        @Override
        public List<ChatMessage> getMessages(Object memoryId) {
            String json = map.get((String) memoryId);
            return messagesFromJson(json);
        }

        @Override
        public void updateMessages(Object memoryId, List<ChatMessage> messages) {
            String json = messagesToJson(messages);
            map.put((String) memoryId, json);
            db.commit();
        }

        @Override
        public void deleteMessages(Object memoryId) {
            map.remove((String) memoryId);
            db.commit();
        }
    }
}