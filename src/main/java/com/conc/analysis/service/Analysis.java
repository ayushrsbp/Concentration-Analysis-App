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

    @Autowired
    private ExcelService excelService;

    public Result analyzeConcentration(InputData inputData) throws IOException {
        // Process the input data
        MultipartFile file = inputData.getFile();
        Double entryFlowRate = inputData.getEnterAirFlowRate();
        Double emissionRate = inputData.getEmissionRate();
        int segmentLength = inputData.getSegmentLength();

        double[] leakageFlowRate = excelService.processExcel(file);

        int n = leakageFlowRate.length;
        double[] concentrationInDuct = new double[n+2];
        double[] concentrationInMine = new double[n+2];

        double[] intakeFlowRate = new double[n+2];
        double[] returnFlowRate = new double[n+2];

        int[] distance = new int[n+2];

        intakeFlowRate[0] = entryFlowRate;
        returnFlowRate[0] = entryFlowRate;

        double prevQinDuct = entryFlowRate, prevQinMine = entryFlowRate;
        double prevConcInDuct = 0.0, prevConcInMine = emissionRate/entryFlowRate;

        concentrationInDuct[0] = prevConcInDuct;
        concentrationInMine[0] = prevConcInMine;

        for(int i = 1; i <= n; i++) {
            if(leakageFlowRate[i-1] < 0.0) {
                concentrationInDuct[i] = prevConcInDuct;
                concentrationInMine[i] = (prevConcInMine * prevQinMine + concentrationInDuct[i] * leakageFlowRate[i-1]) / (prevQinMine + leakageFlowRate[i-1]);
            }
            else {
                concentrationInMine[i] = prevConcInMine;
                concentrationInDuct[i] = (prevConcInDuct * prevQinDuct - concentrationInMine[i] * leakageFlowRate[i-1]) / (prevQinDuct - leakageFlowRate[i-1]);
            }
            prevConcInDuct = concentrationInDuct[i];
            prevConcInMine = concentrationInMine[i];

            prevQinDuct -= leakageFlowRate[i-1];
            prevQinMine += leakageFlowRate[i-1];

            intakeFlowRate[i] = prevQinDuct;
            returnFlowRate[i] = prevQinMine;

            distance[i] = i * segmentLength;
        }

        double concAtFace = prevConcInDuct + (emissionRate / entryFlowRate);

        concentrationInDuct[n+1] = concAtFace;
        concentrationInMine[n+1] = concAtFace;

        intakeFlowRate[n+1] = prevQinDuct;
        returnFlowRate[n+1] = prevQinMine;

        distance[n+1] = (n+1) * segmentLength;

        result.setConcInDuct(concentrationInDuct);
        result.setConcInMine(concentrationInMine);
        result.setConcentrationAtFace(concAtFace);
        result.setIntakeFlowRate(intakeFlowRate);
        result.setReturnFlowRate(returnFlowRate);
        result.setDistance(distance);

        return result;
    }
}
