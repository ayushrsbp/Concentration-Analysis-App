package com.conc.analysis.service;

import com.conc.analysis.form.InputData3;
import com.conc.analysis.form.InputData2;

import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class Optimize {
    @Autowired
    private InputData2 inputData2;

    public void otimize(InputData3 inputData) {
        int noOfDuctSegments = inputData.getNoOfDuctSegments();
        double emissionRate = inputData.getEmissionRate();
        int segmentLength = inputData.getSegmentLength();
        double frictionFactor = inputData.getFrictionFactor();
        double a = inputData.getA();
        double b = inputData.getB();
        double allowedConcentrationMin = inputData.getAllowedConcentrationMin();
        double allowedConcentrationMax = inputData.getAllowedConcentrationMax();
        int fanCountMin = inputData.getFanCountMin();
        int fanCountMax = inputData.getFanCountMax();
        double[][] ducts = inputData.getDucts();

        Arrays.sort(ducts, (x, y) -> Double.compare(x[0], y[0]));

        for(int fan = fanCountMin; fan <= fanCountMax; fan++) {
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
            int curr = 1;
            int loadPerFan = noOfDuctSegments / fan;
            int extra = noOfDuctSegments % fan;
            for(int i = 0; i < fan; i++) {
                fanPos[i] = curr;
                curr += loadPerFan;
            }
            // int fanPerExtra = extra / fan;
            // for(int i = 0; i < extra; i++) {

            // }
            inputData2.setFanPos(fanPos);

            int l = 0, r = ducts.length-1;
            double optimumDia = -1;
            double optimumFanCount = -1;
            double optimumConcentration = -1;
            while(l <= r) {
                int mid = l+(r-l)/2;
                double diameter = ducts[mid][0];
                double s = Math.PI*diameter*segmentLength;
                double area = (Math.PI*diameter*diameter)/4;

                double DuctSegmentResistance = (frictionFactor*s)/Math.pow(area, 3);



            }
        }
    }
}
