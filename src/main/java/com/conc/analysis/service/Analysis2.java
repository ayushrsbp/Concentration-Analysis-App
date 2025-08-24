package com.conc.analysis.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.conc.analysis.form.InputData;
import com.conc.analysis.form.NetForm;
import com.conc.analysis.form.InputData2;
import com.conc.analysis.results.Result;
import com.conc.analysis.results.NetResult;

@Scope("prototype")
@Service
public class Analysis2 {

    @Autowired
    private CreateInputFile createInputFile;

    @Autowired
    private Analysis analysis;

    @Autowired
    private NetSolver netSolver;
    // private NetworkAnalysis netAnalysis;
    // private NetAnalysis netAnalysis;

    @Autowired
    private InputData inputData2;

    public Result analyze(InputData2 inputData) throws IOException {

        double emissionRate = inputData.getEmissionRate();
        int segmentLength = inputData.getSegmentLength();


        NetForm netForm = createInputFile.createInputFile(inputData);

        NetResult netResult = netSolver.solve(netForm);

        double[] q = netResult.getQ();
        int[] no = netResult.getNo();

        double entryFlowRate = q[0];
        List<Double> leakageList = new ArrayList<>();
        for(int i = no.length-2; i >= 0; i--) {
            leakageList.add(q[i]);
            if(no[i] == 3) {
                break;
            }
        }

        double[] leakageFlowRate = new double[leakageList.size()];
        int p = 0;
        for(double flowRate : leakageList) {
            leakageFlowRate[p++] = flowRate;
        }
        System.out.println("Checking of input data for conc analysis");
        System.out.println(entryFlowRate);
        for(int i = 0; i < leakageFlowRate.length; i++) {
            System.out.println(leakageFlowRate[i]);
        }
        inputData2.setEmissionRate(emissionRate);
        inputData2.setSegmentLength(segmentLength);
        inputData2.setEnterAirFlowRate(entryFlowRate);
        inputData2.setLeakageFlowRate(leakageFlowRate);

        // Run net.exe
        // String dir = "src\\main\\resources\\static\\outputFiles";
        // ProcessBuilder pb = new ProcessBuilder("src\\main\\resources\\static\\outputFiles\\net.exe");
        // pb.directory(new File(dir)); // Set working directory
        // pb.redirectErrorStream(true);
        // Process process = pb.start();
        // System.out.println("Running net.exe...");

        // Wait for 2 seconds
        // try {
        //     Thread.sleep(2000);
        // } catch (InterruptedException e) {
        //     Thread.currentThread().interrupt();
        //     throw new RuntimeException("Thread was interrupted while waiting for net.exe to finish.", e);
        // }

        // Read output file
        // InputData inputdata2 = readOutputFile.read();
        // inputdata2.setEmissionRate(emissionRate);
        // inputdata2.setSegmentLength(segmentLength);

        Result result = analysis.analyzeConcentration(inputData2);
        result.setFanCount(inputData.getNfb());
        result.setDuctSegmentCount(inputData.getNoOfDuctSegments());
        result.setDuctSegmentResistance(inputData.getDuctSegmentResistance());
        result.setLeakageResistance(inputData.getLeakageResistance());

        return result;
    }
}
