package com.conc.analysis.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conc.analysis.form.NetForm;
import com.conc.analysis.results.NetResult;

@Service
public class NetworkAnalysis {

    @Autowired
    private NetResult netResult;

    // Equivalent Java declarations
    // static final int MAX_NODES = 80;
    // static final int MAX_BRANCHES = 170;
    // static final int MAX_FIXED = 20;

    static final int MAX_NODES = 2000;
    static final int MAX_BRANCHES = 2000;
    static final int MAX_FIXED = 2000;

    int[] nffo = new int[MAX_FIXED], nfft = new int[MAX_FIXED];
    double[] rff = new double[MAX_FIXED], qff = new double[MAX_FIXED], preg = new double[MAX_FIXED];
    int[] no = new int[MAX_BRANCHES], nt = new int[MAX_BRANCHES];
    double[] r = new double[MAX_BRANCHES], c = new double[MAX_BRANCHES];
    double[] q = new double[MAX_BRANCHES], qold = new double[MAX_BRANCHES], fb = new double[MAX_BRANCHES];
    double[] h = new double[MAX_NODES], rhs = new double[MAX_NODES], xflo = new double[MAX_NODES];
    // int[] kp = new int[MAX_NODES + 1], ja = new int[230], jb = new int[230], in = new int[230];
    int[] kp = new int[MAX_NODES + 1], ja = new int[2300], jb = new int[2300], in = new int[2300];
    double[][] b = new double[MAX_NODES][MAX_NODES];
    // double[] a = new double[10];
    double[] a = new double[100];

    int nff, nfb, nn, iter;
    double err;

    public NetResult solve(NetForm input) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter("oup.txt"))) {

            // String[] tokens = reader.readLine().trim().split("\\s+");
            
            nff = input.getNff();
            nfb = input.getNfb();
            nn = input.getNn();
            iter = input.getIter();
            err = input.getErr();

            for (int i = 0; i < nn; i++) {                 
                h[i] = 0;
                xflo[i] = 0;
            }
            int[] temp_nffo = input.getNffo();
            int[] temp_nfft = input.getNfft();
            double[] temp_rff = input.getRff();
            double[] temp_qff = input.getQff();
            for (int i = 0; i < nff; i++) {
                nffo[i] = temp_nffo[i];
                nfft[i] = temp_nfft[i];
                rff[i] = temp_rff[i];
                qff[i] = temp_qff[i];
                xflo[nffo[i]] -= qff[i];
                xflo[nfft[i]] += qff[i];
            }
            int[] temp_no = input.getNo();
            int[] temp_nt = input.getNt();
            double[] temp_r = input.getR();
            // a = new double[no.length];
            // fb = new double[no.length];
            double[] temp_a = input.getA();
            double[] temp_fb = input.getFb();
            for (int i = 0; i < nfb; i++) {
                no[i] = temp_no[i];
                nt[i] = temp_nt[i];
                double r1 = temp_r[i];
                a[i] = temp_a[i];
                fb[i] = temp_fb[i];
                // tokens = reader.readLine().trim().split("\\s+");
                // no[i] = Integer.parseInt(tokens[0]) - 1;
                // nt[i] = Integer.parseInt(tokens[1]) - 1;
                // double r1 = Double.parseDouble(tokens[2]);
                // a[i] = Double.parseDouble(tokens[3]);
                // fb[i] = Double.parseDouble(tokens[4]);
                r[i] = r1 - a[i];
                qold[i] = 0;
            }

            int nbn = nfb;
            while(nbn < temp_no.length) {
                no[nbn] = temp_no[nbn];
                nt[nbn] = temp_nt[nbn];
                r[nbn] = temp_r[nbn];
                fb[nbn] = 0;
                qold[nbn] = 0;
                nbn++;
            }
            
            // while (true) {
            //     String line = reader.readLine();
            //     if (line == null || line.trim().isEmpty()) break;
            //     tokens = line.trim().split("\\s+");
            //     if (tokens.length < 3) break;
            //     no[nbn] = Integer.parseInt(tokens[0]) - 1;
            //     nt[nbn] = Integer.parseInt(tokens[1]) - 1;
            //     r[nbn] = Double.parseDouble(tokens[2]);
            //     fb[nbn] = 0;
            //     qold[nbn] = 0;
            //     nbn++;
            // }
            // Build incidence matrix
            int k = 0;
            kp[0] = 0;
            for (int j = 0; j < nn; j++) {
                for (int l = 0; l < nbn; l++) {
                    if (no[l] == j) {
                        ja[k] = nt[l];
                        jb[k] = 1;
                        in[k++] = l;
                    } else if (nt[l] == j) {
                        ja[k] = no[l];
                        jb[k] = -1;
                        in[k++] = l;
                    }
                }
                kp[j + 1] = k;
            }

            for (int itr = 0; itr < iter; itr++) {
                for (int j = 0; j < nbn; j++) {
                    if (itr == 0) {
                        c[j] = 1.0 / r[j];
                    } else if (itr == 1) {
                        c[j] = 1.0 / (Math.abs(q[j]) * r[j]);
                        qold[j] = q[j];
                    } else {
                        double qa = Math.sqrt(Math.abs(qold[j] * q[j]));
                        c[j] = 1.0 / (qa * r[j]);
                        qold[j] = qa * q[j] / Math.abs(q[j]);
                    }
                }

                for (int i = 0; i < nn; i++) {
                    double sm = 0, su = 0;
                    Arrays.fill(b[i], 0);
                    for (int m = kp[i]; m < kp[i + 1]; m++) {
                        sm += c[in[m]];
                        su += jb[m] * c[in[m]] * fb[in[m]];
                        b[i][ja[m]] -= c[in[m]];
                    }
                    b[i][i] = sm;
                    rhs[i] = xflo[i] - su;
                }

                b[0][0] = 1;
                rhs[0] = 0;
                for (int j = 1; j < nn; j++) {
                    b[0][j] = b[j][0] = 0;
                }

                choleskySolve(b, h, rhs, nn);

                for (int l = 0; l < nbn; l++) {
                    q[l] = c[l] * ((h[no[l]] - h[nt[l]]) + fb[l]);
                }

                boolean converged = true;
                for (int j = 0; j < nbn; j++) {
                    if (Math.abs(qold[j] - q[j]) > err) {
                        converged = false;
                        break;
                    }
                }
                if (converged) {
                    writer.println("Solution obtained at iteration " + (itr + 1));
                    int[] resNo = new int[nbn];
                    int[] resNt = new int[nbn];
                    double[] resR = new double[nbn];
                    double[] resQ = new double[nbn];
                    double[] resPo = new double[nbn];
                    double[] resPt = new double[nbn];
                    double[] resQmin = new double[nbn];
                    for (int i = 0; i < nbn; i++) {
                        double qMin = q[i] * 60;
                        if (i < nfb) {
                            r[i] += a[i];
                        }
                        resNo[i] = no[i];
                        resNt[i] = nt[i];
                        resR[i] = Math.round(r[i] * 100000)/100000.0;
                        resQ[i] = Math.round(q[i] * 100)/100.0;
                        resPo[i] = Math.round(h[no[i]] * 10)/10.0;
                        resPt[i] = Math.round(h[nt[i]] * 10)/10.0;
                        resQmin[i] = Math.round(qMin * 10)/10.0;
                        // writer.printf("%3d %4d %4d %7.2f %7.2f %7.3f %9.5f %8.2f\n",
                        //         i + 1, no[i], nt[i], h[no[i]], h[nt[i]], q[i], r[i], qMin);
                        writer.printf("" + (i + 1) + " " + no[i] + " " + nt[i] + " " + h[no[i]] + " " + h[nt[i]] + " " + q[i] + " " + r[i] + " " + qMin + "\n");
                    }
                    netResult.setNo(resNo);
                    netResult.setNt(resNt);
                    netResult.setPo(resPo);
                    netResult.setPt(resPt);
                    netResult.setQ(resQ);
                    netResult.setR(resR);
                    netResult.setQMin(resQmin);
                    break;
                }
            }
        }
        return netResult;
    }

    void choleskySolve(double[][] a, double[] x, double[] rhs, int n) {
        double[] y = new double[n];
        double[][] d = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, d[i], 0, n);
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                double sum = d[i][j];
                for (int k = 0; k < j; k++) {
                    sum -= d[i][k] * d[j][k];
                }
                if (i == j) {
                    d[i][j] = Math.sqrt(sum); 
                }else {
                    d[i][j] = sum / d[j][j];
                }
            }
            for (int j = i + 1; j < n; j++) {
                d[i][j] = 0;
            }
        }

        for (int i = 0; i < n; i++) {
            double sum = rhs[i];
            for (int j = 0; j < i; j++) {
                sum -= d[i][j] * y[j];
            }
            y[i] = sum / d[i][i];
        }
        for (int i = n - 1; i >= 0; i--) {
            double sum = y[i];
            for (int j = i + 1; j < n; j++) {
                sum -= d[j][i] * x[j];
            }
            x[i] = sum / d[i][i];
        }
    }
}
