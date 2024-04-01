package com.chatbot.config;

import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EmbeddedInjestorConfig {

    @NotNull
    public static EmbeddedStoreInjestorConfig getEmbeddedStoreInjestorConfig() {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(500, 0))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore).build();
        EmbeddedStoreInjestorConfig embeddedStoreInjestorConfig = new EmbeddedStoreInjestorConfig(embeddingModel, embeddingStore, ingestor);
        return embeddedStoreInjestorConfig;
    }

    public record EmbeddedStoreInjestorConfig(EmbeddingModel embeddingModel,
                                               EmbeddingStore<TextSegment> embeddingStore,
                                               EmbeddingStoreIngestor ingestor) {
    }
}
