package com.conc.analysis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

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
    public String aboutMachine() {
        String os = System.getProperty("os.name");
        String version = System.getProperty("os.version");
        String architecture = System.getProperty("os.arch");
        String javaVersion = System.getProperty("java.version");
        String user = System.getProperty("user.name");
        return "OS: " + os + ", Version: " + version + ", Architecture: " + architecture + ", javaVersion: " + javaVersion + ", user: " + user + "";
    }
}
