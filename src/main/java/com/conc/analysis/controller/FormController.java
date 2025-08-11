package com.conc.analysis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import com.conc.analysis.form.InputData;
import com.conc.analysis.results.Result;
import com.conc.analysis.service.NetAnalysis;
import com.conc.analysis.service.Analysis;
import com.conc.analysis.service.Analysis2;
import com.conc.analysis.service.Optimize;
import com.conc.analysis.form.InputData2;
import com.conc.analysis.form.InputData3;
import com.conc.analysis.form.NetForm;
import com.conc.analysis.results.NetResult;

@Controller
public class FormController {

    @Autowired
    private NetAnalysis networkAnalysis;

    @Autowired
    private Analysis analysis;

    @Autowired
    private Analysis2 analysis2;

    @Autowired
    private Optimize optimize;

    @PostMapping("/netAnalysis")
    public String netAnalysis(NetForm input, Model model) throws IOException {
        NetResult result = networkAnalysis.solve(input);
        model.addAttribute("result", result);
        return "netResult";
        // This method can be used to handle net analysis if needed
    }

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
        return "result";
    }

    @PostMapping("/optimize")
    public String problem2(@ModelAttribute InputData3 inputData, Model model) throws IOException {
        Result result = optimize.optimize(inputData);
        model.addAttribute("result", result);
        return "result";
    }
}
