package com.conc.analysis.chatbot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.conc.analysis.chatbot.model.ChatDtos.ChatRequest;
import com.conc.analysis.chatbot.model.ChatDtos.ChatResponse;
import com.conc.analysis.chatbot.rag.SearchService;
import com.conc.analysis.chatbot.service.Groqclient;
import com.conc.analysis.chatbot.service.MathService;
import com.conc.analysis.chatbot.service.PromptBuilder;
import com.conc.analysis.chatbot.util.Stopwatch;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final SearchService searchService;
    private final Groqclient groqClient;

    public ApiController(SearchService searchService, Groqclient groqClient) {
        this.searchService = searchService;
        this.groqClient = groqClient;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest req) {
        try (var sw = Stopwatch.startNew()) {
            var math = MathService.tryEvaluate(req.getMessage());
            var retrieval = searchService.retrieve(req.getMessage());
            String system = PromptBuilder.systemPrompt();
            String user = PromptBuilder.userPrompt(req.getMessage(), retrieval.context, retrieval.sources, math);
            String answer = groqClient.chat(system, user);
            return ResponseEntity.ok(new ChatResponse(answer, retrieval.sources, sw.elapsedMs()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ChatResponse("Error: " + e.getMessage(), List.of(), 0));
        }
    }
}

