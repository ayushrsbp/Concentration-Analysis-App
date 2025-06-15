package com.conc.analysis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import com.conc.analysis.form.InputData;
import com.conc.analysis.results.Result;
import com.conc.analysis.service.Analysis;

@Controller
public class FormController {

    @Autowired
    private Analysis analysis;

    @PostMapping("/process")
    public String processInput(@ModelAttribute InputData inputData,
                               Model model) throws IOException {
        Result result = analysis.analyzeConcentration(inputData);
        model.addAttribute("result", result);
        return "result";
    }

}
