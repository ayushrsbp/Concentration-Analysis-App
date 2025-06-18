package com.conc.analysis.service;


import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.conc.analysis.service.CreateInputFile;
import com.conc.analysis.form.InputData2;

import java.io.File;
import java.io.IOException;

@Service
public class Analysis2 {

    @Autowired
    private CreateInputFile createInputFile;

    public void analyze(InputData2 inputData) throws IOException {
        createInputFile.createInputFile(inputData);

        // 🔧 Run net.exe
        String dir = "src/main/resources/static/outputFiles";
        ProcessBuilder pb = new ProcessBuilder("net.exe");
        pb.directory(new File(dir)); // Set working directory
        pb.redirectErrorStream(true);
        Process process = pb.start();

    }
}
