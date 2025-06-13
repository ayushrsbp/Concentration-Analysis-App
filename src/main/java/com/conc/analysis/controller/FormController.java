package com.conc.analysis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.conc.analysis.form.InputData;

@Controller
public class FormController {

    @PostMapping("/process")
    public String processInput(@ModelAttribute InputData inputData,
                               Model model) {
        
        return "result-view";
    }

}
