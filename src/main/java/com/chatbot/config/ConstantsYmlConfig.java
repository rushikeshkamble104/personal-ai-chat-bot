package com.chatbot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ConstantsYmlConfig {

    @Value("${sourcePath}")
    String sourcePath;
    @Value("${destinationPath}")
    String destinationPath;
}
