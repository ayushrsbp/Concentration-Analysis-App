package com.conc.analysis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.conc.analysis.service.MachineInfo;
import com.conc.analysis.results.Info;

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
}
