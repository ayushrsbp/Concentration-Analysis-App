package com.conc.analysis.chatbot.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Groqclient {

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${groq.apiBase}")
    private String apiBase;

    @Value("${groq.apiKey:}")
    private String apiKeyFromProps;

    @Value("${groq.model}")
    private String model;

    private String apiKey() {
        String env = System.getenv("GROQ_API_KEY");
        return (env != null && !env.isBlank()) ? env : apiKeyFromProps;
    }

    public String chat(String system, String user) throws Exception {
        String key = apiKey();
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("GROQ_API_KEY not set. Set env var or groq.apiKey in properties.");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role","system","content", system));
        messages.add(Map.of("role","user","content", user));
        body.put("messages", messages);
        body.put("temperature", 0.2);
        body.put("max_tokens", 1024);

        String json = mapper.writeValueAsString(body);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(apiBase + "/chat/completions"))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 300) {
            throw new RuntimeException("Groq API error: " + resp.statusCode() + " - " + resp.body());
        }

        JsonNode root = mapper.readTree(resp.body());
        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.isEmpty()) {
            return "No completion returned.";
        }
        return choices.get(0).path("message").path("content").asText();
    }
}


