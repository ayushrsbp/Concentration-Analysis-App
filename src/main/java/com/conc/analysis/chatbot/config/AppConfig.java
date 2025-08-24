package com.conc.analysis.chatbot.config;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public Analyzer analyzer() {
        // Good general-purpose analyzer for mixed technical content
        return new StandardAnalyzer();
    }
}

