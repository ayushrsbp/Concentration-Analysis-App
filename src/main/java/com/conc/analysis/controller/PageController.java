package com.conc.analysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.conc.analysis.results.Info;
import com.conc.analysis.results.Result;
import com.conc.analysis.service.MachineInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PageController {

    @Autowired
    private MachineInfo machineInfo;

    @RequestMapping("/")
    public String home() {
        return "index";
    }

    @RequestMapping("/net")
    public String net() {
        return "net";
    }

    @RequestMapping("/problem1")
    public String testing() {
        return "problem";
    }

    @RequestMapping("/problem2")
    public String problem2() {
        return "problem2";
    }

    @GetMapping("/info")
    public String aboutMachine(Model model) {
        Info info = machineInfo.getInfo();
        model.addAttribute("info", info);
        return "machineInfo";
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @RequestMapping("/show")
    public String show(String resultJson, Model model) {
        try {
            // Deserialize JSON back to Result object
            Result result = objectMapper.readValue(resultJson, Result.class);

            // Pass to Thymeleaf view
            model.addAttribute("result", result);
            return "result";  // showResult.html (your new Thymeleaf template)
        } catch (Exception e) {
            model.addAttribute("error", "Error parsing result: " + e.getMessage());
            return "error";
        }
    }
}
