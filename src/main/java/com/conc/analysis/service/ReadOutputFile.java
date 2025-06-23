package com.conc.analysis.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conc.analysis.form.InputData;

@Service
public class ReadOutputFile {

    @Autowired
    private InputData inputData;

    public InputData read() throws IOException {
        Path path = Paths.get("src\\main\\resources\\static\\outputFiles\\oup.txt");
        List<String> lines = Files.readAllLines(path);

        List<double[]> parsedRows = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("-") || line.startsWith("br.no")) continue;

            // Split on whitespace, handle variable spacing
            String[] parts = line.trim().split("\\s+");

            if (parts.length >= 6) {
                try {
                    double[] rowData = new double[parts.length];
                    for (int i = 0; i < 6; i++) {
                        rowData[i] = Double.parseDouble(parts[i]);
                    }
                    parsedRows.add(rowData);
                } catch (NumberFormatException e) {
                    // Skip lines that don't contain numeric values
                }
            }
        }

        // Step 1: enterAirFlowRate = abs(value of 6th column of last row)
        double enterAirFlowRate = Math.abs(parsedRows.get(parsedRows.size() - 1)[5]);

        // Step 2: collect 6th column values from last-1 to row where 2nd column == 3
        List<Double> leakageList = new ArrayList<>();
        for (int i = parsedRows.size() - 2; i >= 0; i--) {
            double[] row = parsedRows.get(i);
            leakageList.add(row[5]);
            if ((int) row[1] == 3) break;
        }

        // Convert to array
        double[] leakageFlowRate = new double[leakageList.size()];
        for (int i = 0; i < leakageList.size(); i++) {
            leakageFlowRate[i] = leakageList.get(i);
        }

        // Output for verification
        System.out.println("Enter Air Flow Rate: " + enterAirFlowRate);
        System.out.println("Leakages Count = " + leakageFlowRate.length);
        System.out.println("Leakage Flow Rates:");
        for (double v : leakageFlowRate) {
            System.out.println("  " + v);
        }
        // Update inputData
        inputData.setEnterAirFlowRate(enterAirFlowRate);
        inputData.setLeakageFlowRate(leakageFlowRate);

        return inputData;

    }
}

