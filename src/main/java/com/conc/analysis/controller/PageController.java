package com.conc.analysis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/home")
    public String home() {
        return "airquantitydata";
    }

    @RequestMapping("/testing")
    public String testing() {
        return "problem";
    }
}
