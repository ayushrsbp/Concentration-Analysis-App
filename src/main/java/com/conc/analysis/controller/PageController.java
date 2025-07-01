package com.conc.analysis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/home")
    public String home() {
        return "home";
    }

    @RequestMapping("/problem1")
    public String testing() {
        return "problem";
    }

    @RequestMapping("/problem2")
    public String problem2() {
        return "problem2";
    }
}
