package com.conc.analysis.service;

import java.io.*;
import java.util.HashSet;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.conc.analysis.form.InputData2;

@Service
public class CreateInputFile {

    public void createInputFile(InputData2 inputdata) throws IOException {
        int nff = inputdata.getNff();
        int nfb = inputdata.getNfb();
        int iter = inputdata.getIter();
        double err = inputdata.getErr();
        int noOfDuctSegments = inputdata.getNoOfDuctSegments();
        double a = inputdata.getA();
        double b = inputdata.getB();
        double ductSegmentResistance = inputdata.getDuctSegmentResistance();
        double leakageResistance = inputdata.getLeakageResistance();
        int[] fanPos = inputdata.getFanPos();
        int nn = noOfDuctSegments + nfb + 2;

        String filePath = "src\\main\\resources\\static\\outputFiles\\inp.txt";
        FileWriter fw = new FileWriter(filePath);
        BufferedWriter writer = new BufferedWriter(fw);

        // First line
        writer.write(nff + " " + nfb + " " + nn + " " + iter + " " + String.format(Locale.US, "%.3f", err));
        writer.newLine();

        // Next nff lines for each fixed flow branch
        for (int i = 0; i < nff; i++) {
            writer.newLine();
        }

        // Next nfb lines for each fan branch
        HashSet<Integer> fanTailPos = new HashSet<>();
        HashSet<Integer> fanHeadPos = new HashSet<>();

        for (int i = 0; i < nfb; i++) {
            int fanTail = nn - fanPos[i] + 1 - i;
            int fanHead = nn - fanPos[i] - i;
            fanTailPos.add(fanTail);
            fanHeadPos.add(fanHead);
            writer.write(fanTail + " " + fanHead + " 0 " +
                String.format(Locale.US, "%.3f", a) + " " +
                String.format(Locale.US, "%.3f", b));
            writer.newLine();
        }

        // Next lines for each duct segment
        for (int i = nn - 1; i > 3; i--) {
            if (!fanTailPos.contains(i)) {
                writer.write(i + " " + (i - 1) + " " +
                    String.format(Locale.US, "%.3f", ductSegmentResistance));
                writer.newLine();
            }
        }

        writer.write("3 2 " + String.format(Locale.US, "%.3f", ductSegmentResistance / 2.0) + "\n");
        writer.write("2 1 " + String.format(Locale.US, "%.3f", ductSegmentResistance / 2.0) + "\n");

        // Next lines for each leakage branch
        for (int i = 3; i < nn; i++) {
            if (!fanHeadPos.contains(i)) {
                writer.write(i + " 1 " + String.format(Locale.US, "%.3f", leakageResistance));
                writer.newLine();
            }
        }

        // Final line
        writer.write(nn + " 1 " + String.format(Locale.US, "%.7f", 0.000001));

        writer.close();
        System.out.println("Input file created at " + filePath);
    }
}
