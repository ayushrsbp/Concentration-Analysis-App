package com.conc.analysis.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.conc.analysis.entities.DuctSpecification;
import com.conc.analysis.form.InputData2;
import com.conc.analysis.form.InputData3;
import com.conc.analysis.results.OptimizeResult;
import com.conc.analysis.results.Result;

@Scope("prototype")
@Service
public class Optimize {
    @Autowired
    private InputData2 inputData2;

    @Autowired
    private Analysis2 analysis2;

    @Autowired
    private OptimizeResult optimizeResult;

    @Autowired
    private DuctSpecification ductSpecification;

    public OptimizeResult optimize(InputData3 inputData) throws IOException {
        double emissionRate = inputData.getEmissionRate();
        double a = inputData.getA();
        double b = inputData.getB();
        double[] ductDiameters = inputData.getDuctDiameters();
        double[] frictionFactor = inputData.getFrictionFactor();
        int[] segmentLength = inputData.getSegmentLength();
        int[] segmentCount = inputData.getSegmentCount();
        double[] leakageResistance = inputData.getLeakageResistance();


        
        int noOfDuctType = ductDiameters.length;
        DuctSpecification[] ductData = new DuctSpecification[noOfDuctType];

        List<Double>[] concentrations = new ArrayList[noOfDuctType];
        List<Double>[] airFlowRateAtFace = new ArrayList[noOfDuctType];
        List<Double>[] airFlowRateAtEntry = new ArrayList[noOfDuctType];
        List<Result>[] allResults = new ArrayList[noOfDuctType];
        for(int i = 0 ; i< noOfDuctType; i++) {
            int maxFan =(segmentCount[i]*segmentLength[i])/((100/segmentLength[i])*segmentLength[i]);
            concentrations[i] = new ArrayList<>();
            airFlowRateAtFace[i] = new ArrayList<>();
            airFlowRateAtEntry[i] = new ArrayList<>();
            allResults[i] = new ArrayList<>();
            for(int fan = 1; fan <= maxFan; fan++) {
                int nff = 0;
                int nfb = fan;
                int iter = 40;
                double err = 0.0005;
                int noOfDuctSegments = segmentCount[i];
                // double a, b;
                double f = frictionFactor[i];
                double s = Math.PI*ductDiameters[i]*segmentLength[i];
                double area = (Math.PI * ductDiameters[i] * ductDiameters[i])/4;
                double ductSegmentResistance = Math.round((f * s *1000)/Math.pow(area, 3))/1000.0;
                // double leakageResistance;
                int[] fanPos = new int[fan];
                fanPos[0] = 1;
                for(int pos = 1; pos < fan; pos++) {
                    fanPos[pos] = fanPos[pos-1] + noOfDuctSegments/fan;
                }
                // double emissionRate;
                int l = segmentLength[i];

                inputData2.setNff(nff);
                inputData2.setNfb(nfb);
                inputData2.setIter(iter);
                inputData2.setErr(err);
                inputData2.setNoOfDuctSegments(noOfDuctSegments);
                inputData2.setA(a);
                inputData2.setB(b);
                inputData2.setDuctSegmentResistance(ductSegmentResistance);
                inputData2.setLeakageResistance(leakageResistance[i]*(l/100.0));
                inputData2.setFanPos(fanPos);
                inputData2.setEmissionRate(emissionRate);
                inputData2.setSegmentLength(l);

                Result result = analysis2.analyze(inputData2);

                allResults[i].add(result);
                concentrations[i].add(result.getConcentrationAtFace());
                airFlowRateAtEntry[i].add(result.getEnterAirFlowRate());
                double[] returnFlowRate = result.getReturnFlowRate();
                airFlowRateAtFace[i].add(returnFlowRate[returnFlowRate.length-1]);
            }
            ductData[i] = new DuctSpecification();
            ductData[i].setDuctDiameters(ductDiameters[i]);
            ductData[i].setFrictionFactor(frictionFactor[i]);
            ductData[i].setLeakageResistance(leakageResistance[i]);
            ductData[i].setSegmentCount(segmentCount[i]);
            ductData[i].setSegmentLength(segmentLength[i]);
        }
        optimizeResult.setConcentrations(concentrations);
        optimizeResult.setAirFlowRateAtFace(airFlowRateAtFace);
        optimizeResult.setAirFlowRateAtEntry(airFlowRateAtEntry);
        optimizeResult.setDuctSpecification(ductData);
        optimizeResult.setAllResults(allResults);
        optimizeResult.setMaxConc(inputData.getMaxConc());
        optimizeResult.setEmissionRate(emissionRate);
        optimizeResult.setA(a);
        optimizeResult.setB(b);

        return optimizeResult;
    }
}
