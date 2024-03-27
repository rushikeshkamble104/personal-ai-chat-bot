package com.chatbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;

/**
 *  embedding model properties
 *
 */
@Getter
@Setter
class EmbeddingModelProperties {

    String baseUrl;
    String apiKey;
    String organizationId;
    String modelName;
    Integer dimensions;
    String user;
    Duration timeout;
    Integer maxRetries;
    @NestedConfigurationProperty
    ProxyProperties proxy;
    Boolean logRequests;
    Boolean logResponses;
}