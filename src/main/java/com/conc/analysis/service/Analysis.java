package com.conc.analysis.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.conc.analysis.form.InputData;
import com.conc.analysis.results.Result;

@Service
public class Analysis {

    private Result result = new Result();

    @Autowired
    private ExcelService excelService;

    public Result analyzeConcentration(InputData inputData) throws IOException {
        // Process the input data
        // MultipartFile file = inputData.getFile();
        Double entryFlowRate = inputData.getEnterAirFlowRate();
        Double emissionRate = inputData.getEmissionRate();
        int segmentLength = inputData.getSegmentLength();

        // double[] leakageFlowRate = excelService.processExcel(file);
        double[] leakageFlowRate = inputData.getLeakageFlowRate();
        int n = leakageFlowRate.length;
        double[] concentrationInDuct = new double[n+2];
        double[] concentrationInMine = new double[n+2];

        double[] intakeFlowRate = new double[n+2];
        double[] returnFlowRate = new double[n+2];

        int[] distance = new int[n+2];

        intakeFlowRate[0] = entryFlowRate;
        returnFlowRate[0] = entryFlowRate;

        double prevQinDuct = entryFlowRate, prevQinMine = entryFlowRate;
        double prevConcInDuct = 0.0, prevConcInMine = Math.round(emissionRate/entryFlowRate * 1000)/1000.0;

        concentrationInDuct[0] = prevConcInDuct;
        concentrationInMine[0] = prevConcInMine;
        concentrationInMine[1]  = prevConcInMine;

        for(int i = 1; i <= n; i++) {
            if(leakageFlowRate[i-1] > 0.0) {
                concentrationInDuct[i] = prevConcInDuct;
                double currConcInMine = (prevConcInMine * prevQinMine - concentrationInDuct[i] * leakageFlowRate[i-1]) / (prevQinMine - leakageFlowRate[i-1]);
                concentrationInMine[i+1] = Math.round(currConcInMine * 1000) / 1000.0;
            }
            else {
                concentrationInMine[i+1] = prevConcInMine;
                double currConcInDuct = (prevConcInDuct * prevQinDuct - concentrationInMine[i+1] * leakageFlowRate[i-1]) / (prevQinDuct - leakageFlowRate[i-1]);
                concentrationInDuct[i] = Math.round(currConcInDuct * 1000) / 1000.0;
            }
            prevConcInDuct = concentrationInDuct[i];
            prevConcInMine = concentrationInMine[i+1];

            prevQinDuct -= leakageFlowRate[i-1];
            intakeFlowRate[i] = Math.round(prevQinDuct * 1000) / 1000.0;
            
            returnFlowRate[i] =  Math.round(prevQinMine * 1000) / 1000.0;
            prevQinMine -= leakageFlowRate[i-1];


            distance[i] = i * segmentLength;
        }

        double concAtFace = concentrationInMine[n+1];

        concentrationInDuct[n+1] = Math.round(concAtFace * 1000) / 1000.0;

        intakeFlowRate[n+1] = Math.round(prevQinDuct * 1000) / 1000.0;
        returnFlowRate[n+1] = Math.round(prevQinMine * 1000) / 1000.0;

        distance[n+1] = (n+1) * segmentLength;

        result.setConcInDuct(concentrationInDuct);
        result.setConcInMine(concentrationInMine);
        result.setConcentrationAtFace(concAtFace);
        result.setLeakages(leakageFlowRate);
        result.setIntakeFlowRate(intakeFlowRate);
        result.setReturnFlowRate(returnFlowRate);
        result.setDistance(distance);

        return result;
    }
}
