package com.conc.analysis.chatbot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @Value("${app.title}")
    private String appTitle;

    @GetMapping("/chat")
    public String index(Model model) {
        model.addAttribute("title", appTitle);
        return "index";
    }

}

