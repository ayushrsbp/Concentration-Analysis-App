package com.conc.analysis.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conc.analysis.form.InputData2;
import com.conc.analysis.form.NetForm;

@Service
public class CreateInputFile {

    @Autowired
    private NetForm netForm;

    public NetForm createInputFile(InputData2 inputdata) throws IOException {
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

        netForm.setNff(nff);
        netForm.setNfb(nfb);
        netForm.setNn(nn);
        netForm.setIter(iter);
        netForm.setErr(err);


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

        netForm.setNffo(new int[nff]);
        netForm.setNfft(new int[nff]);
        netForm.setRff(new double[nff]);
        netForm.setQff(new double[nff]);

        List<Integer> temp_no = new ArrayList<>();
        List<Integer> temp_nt = new ArrayList<>();
        List<Double> temp_r = new ArrayList<>();
        List<Double> temp_a = new ArrayList<>();
        List<Double> temp_fb = new ArrayList<>();

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
            temp_no.add(fanTail);
            temp_nt.add(fanHead);
            temp_r.add(0.0);
            temp_a.add(a);
            temp_fb.add(b);
        }

        // Next lines for each duct segment
        for (int i = nn - 1; i > 3; i--) {
            if (!fanTailPos.contains(i)) {
                writer.write(i + " " + (i - 1) + " " +
                    String.format(Locale.US, "%.3f", ductSegmentResistance));
                writer.newLine();
                temp_no.add(i);
                temp_nt.add(i-1);
                temp_r.add(ductSegmentResistance);
                temp_a.add(0.0);
                temp_fb.add(0.0);
            }
        }

        writer.write("3 2 " + String.format(Locale.US, "%.3f", ductSegmentResistance / 2.0) + "\n");
        temp_no.add(3);
        temp_nt.add(2);
        temp_r.add(ductSegmentResistance);
        temp_a.add(0.0);
        temp_fb.add(0.0);

        writer.write("2 1 " + String.format(Locale.US, "%.3f", ductSegmentResistance / 2.0) + "\n");
        temp_no.add(2);
        temp_nt.add(1);
        temp_r.add(ductSegmentResistance);
        temp_a.add(0.0);
        temp_fb.add(0.0);

        // Next lines for each leakage branch
        for (int i = 3; i < nn; i++) {
            if (!fanHeadPos.contains(i)) {
                writer.write(i + " 1 " + String.format(Locale.US, "%.3f", leakageResistance));
                writer.newLine();

                temp_no.add(i);
                temp_nt.add(1);
                temp_r.add(leakageResistance);
                temp_a.add(0.0);
                temp_fb.add(0.0);
            }
        }

        // Final line
        writer.write(nn + " 1 " + String.format(Locale.US, "%.7f", 0.000001));
        temp_no.add(nn);
        temp_nt.add(1);
        temp_r.add(0.000001);
        temp_a.add(0.0);
        temp_fb.add(0.0);
        
        writer.close();
        System.out.println("Input file created at " + filePath);
        int nBranches = temp_no.size();
        // netForm.setNo(temp_no.toArray(new int[nBranches]));
        // netForm.setNt(temp_nt.toArray(new int[nBranches]));
        // netForm.setR(temp_r.toArray(new double[nBranches]));
        // netForm.setA(temp_a.toArray(new double[nBranches]));
        // netForm.setFb(temp_fb.toArray(new double[nBranches]));
        return netForm;
    }
}
