package com.conc.analysis.service;

import com.conc.analysis.form.InputData;
import com.conc.analysis.results.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import com.conc.analysis.service.ExcelService;

@Service
public class Analysis {

    private Result result = new Result();

    @AutoWired
    private ExcelService excelService;

    public Result analyzeConcentration(InputData inputData) throws IOException {
        // Process the input data
        MultipartFile file = inputData.getFile();
        Double entryFlowRate = inputData.getEnterAirFlowRate();
        Double emissionRate = inputData.getEmissionRate();

        double[] leakageFlowRate = excelService.processExcel(file);

        int n = leakageFlowRate.length;
        double[] concentrationInDuct = new double[n];
        double[] concentrationInMine = new double[n];

        double prevQinDuct = 0.0, prevQinMine = 0.0;
        double prevConcInDuct = 0.0, prevConcInMine = 0.0;

        for(int i = 0; i < n; i++) {
            if(leakageFlowRate[i] < 0.0) {
                concentrationInDuct[i] = prevConcInDuct;
                concentrationInMine[i] = (prevConcInMine * prevQinMine + concentrationInDuct[i] * leakageFlowRate[i]) / (prevQinMine + leakageFlowRate[i]);
            }
            else {
                concentrationInMine[i] = prevConcInMine;
                concentrationInDuct[i] = (prevConcInDuct * prevQinDuct - concentrationInMine[i] * leakageFlowRate[i]) / (prevQinDuct - leakageFlowRate[i]);
            }
        }

        return result;
    }
}
