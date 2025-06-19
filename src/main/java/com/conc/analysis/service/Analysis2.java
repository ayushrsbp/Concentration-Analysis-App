package com.conc.analysis.service;


import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conc.analysis.form.InputData;
import com.conc.analysis.form.InputData2;
import com.conc.analysis.service.Analysis;
import com.conc.analysis.results.Result;


@Service
public class Analysis2 {

    @Autowired
    private CreateInputFile createInputFile;
    
    @Autowired
    private ReadOutputFile readOutputFile;

    @Autowired
    private Analysis analysis;

    public Result analyze(InputData2 inputData) throws IOException {

        double emissionRate = inputData.getEmissionRate();
        int segmentLength = inputData.getSegmentLength();

        createInputFile.createInputFile(inputData);

        // 🔧 Run net.exe
        String dir = "src\\main\\resources\\static\\outputFiles";
        ProcessBuilder pb = new ProcessBuilder("src\\main\\resources\\static\\outputFiles\\net.exe");
        pb.directory(new File(dir)); // Set working directory
        pb.redirectErrorStream(true);
        Process process = pb.start();
        System.out.println("Running net.exe...");

        // Read output file
        InputData inputdata2 = readOutputFile.read();
        inputdata2.setEmissionRate(emissionRate);
        inputdata2.setSegmentLength(segmentLength);

        Result result = analysis.analyzeConcentration(inputdata2);

        return result;
    }
}
