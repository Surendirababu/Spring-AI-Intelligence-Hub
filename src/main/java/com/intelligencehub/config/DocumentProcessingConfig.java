package com.intelligencehub.config;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentProcessingConfig {

    @Value("${document.processing.chunk-size:800}")
    private int chunkSize;

    @Value("${document.processing.chunk-overlap:100}")
    private int chunkOverlap;

    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter(
            chunkSize,      // chunk size
            chunkOverlap,   // overlap
            5,              // min chunk chars
            10000,          // max tokens
            true            // keep separator
        );
    }
}
