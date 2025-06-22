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
import com.conc.analysis.service.Analysis2;
import com.conc.analysis.form.InputData2;

@Controller
public class FormController {

    @Autowired
    private Analysis analysis;

    @Autowired
    private Analysis2 analysis2;

    @PostMapping("/process")
    public String processInput(@ModelAttribute InputData inputData,
                               Model model) throws IOException {
        Result result = analysis.analyzeConcentration(inputData);
        model.addAttribute("result", result);
        return "result";
    }

    @PostMapping("/compute")
    public String find(@ModelAttribute InputData2 inputData,
                       Model model) throws IOException {
        Result result = analysis2.analyze(inputData);
        model.addAttribute("result", result);
        return "result21";
    }
}
