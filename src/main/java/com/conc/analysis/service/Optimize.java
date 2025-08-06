package com.conc.analysis.service;

import com.conc.analysis.form.InputData3;
import com.conc.analysis.form.InputData2;
import com.conc.analysis.results.Result;
import com.conc.analysis.service.Analysis2;

import java.io.IOException;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class Optimize {
    @Autowired
    private InputData2 inputData2;

    @Autowired
    private Analysis2 analysis2;

    public Result optimize(InputData3 inputData) throws IOException {
        int noOfDuctSegments = inputData.getNoOfDuctSegments();
        double emissionRate = inputData.getEmissionRate();
        int segmentLength = inputData.getSegmentLength();
        double frictionFactor = inputData.getFrictionFactor();
        double a = inputData.getA();
        double b = inputData.getB();
        double maxAllowedConcentration = inputData.getMaxAllowedConcentration();
        double[] ducts = inputData.getDuctDiameters();
        // List<Double> ducts = inputData.getDuctDiameters();

        // Collections.sort(ducts);
        Arrays.sort(ducts);
        Result ultimateOptimum = null;
        for(int fan = 0; fan <= (noOfDuctSegments*segmentLength)/250; fan++) {
            inputData2.setNff(0);
            inputData2.setNfb(fan);
            inputData2.setIter(30);
            inputData2.setErr(0.0005);
            inputData2.setA(a);
            inputData2.setB(b);
            inputData2.setNoOfDuctSegments(noOfDuctSegments);
            inputData2.setEmissionRate(emissionRate);
            inputData2.setSegmentLength(segmentLength);
            int[] fanPos = new int[fan];
            if(fan != 0) {
                int curr = 1;
                int loadPerFan = noOfDuctSegments / fan;
                int extra = noOfDuctSegments % fan;
                for(int i = 0; i < fan; i++) {
                    fanPos[i] = curr;
                    curr += loadPerFan;
                }
            }
            // int fanPerExtra = extra / fan;
            // for(int i = 0; i < extra; i++) {

            // }
            inputData2.setFanPos(fanPos);

            int l = 0, r = ducts.length-1;
            Result optimum = null;
            while(l <= r) {
                int mid = l+(r-l)/2;
                double diameter = ducts[mid];
                double s = Math.PI*diameter*segmentLength;
                double area = (Math.PI*diameter*diameter)/4;

                double DuctSegmentResistance = (frictionFactor*s)/Math.pow(area, 3);
                inputData2.setDuctSegmentResistance(DuctSegmentResistance);
                inputData2.setLeakageResistance(DuctSegmentResistance*100);

                Result result = analysis2.analyze(inputData2);
                double concentration = result.getConcentrationAtFace();
                if(concentration > 0 && concentration <= maxAllowedConcentration) {
                    r = mid - 1;
                    optimum = result;
                } else {
                    l = mid + 1;
                }
            }
            if(optimum != null) {
                ultimateOptimum = optimum;
                break;
            }
        }
        return ultimateOptimum;
    }
}
