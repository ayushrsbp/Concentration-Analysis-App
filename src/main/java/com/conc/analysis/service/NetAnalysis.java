package com.conc.analysis.service;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.conc.analysis.form.NetForm;
import com.conc.analysis.results.NetResult;

@Scope("prototype")
@Service
public class NetAnalysis {

    @Autowired
    private NetResult netResult;

    static final int MAX_NODES = 2000;
    static final int MAX_BRANCHES = 2000;
    static final int MAX_FIXED = 2000;

    double[] h = new double[MAX_NODES], rhs = new double[MAX_NODES], xflo = new double[MAX_NODES];
    // double[] r = new double[MAX_BRANCHES];
    double[] c = new double[MAX_BRANCHES];
    double[] q = new double[MAX_BRANCHES], qold = new double[MAX_BRANCHES];
    double[] fb;
    double[] a;
    int[] kp = new int[MAX_NODES + 1], ja = new int[230], jb = new int[230], in = new int[230];
    double[][] b = new double[MAX_NODES][MAX_NODES];

    int nff, nfb, nn, iter;
    double err;
    public NetResult solve(NetForm input) throws IOException{
       try ( PrintWriter writer = new PrintWriter(new FileWriter("oup.txt"))) {

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
            int[] nffo = input.getNffo();
            int[] nfft = input.getNfft();
            double[] rff = input.getRff();
            double[] qff = input.getQff();
            for (int i = 0; i < nff; i++) {
                xflo[nffo[i]] -= qff[i];
                xflo[nfft[i]] += qff[i];
            }
            int[] no = input.getNo();
            int[] nt = input.getNt();
            double[] r = input.getR();
            a = new double[no.length];
            fb = new double[no.length];
            double[] temp_a = input.getA();
            double[] temp_fb = input.getFb();
            for (int i = 0; i < nfb; i++) {
                // tokens = reader.readLine().trim().split("\\s+");
                // no[i] = Integer.parseInt(tokens[0]) - 1;
                // nt[i] = Integer.parseInt(tokens[1]) - 1;
                // double r1 = Double.parseDouble(tokens[2]);
                // a[i] = Double.parseDouble(tokens[3]);
                // fb[i] = Double.parseDouble(tokens[4]);
                a[i] = temp_a[i];
                fb[i] = temp_fb[i];
                r[i] = r[i] - a[i];
                qold[i] = 0;
            }

            // int nbn = nfb;
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
            int nbn = no.length;
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
                    q[l] = Math.round(c[l] * ((h[no[l]] - h[nt[l]]) + fb[l]) * 1000.0)/ 1000.0;
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
                    double[] po = new double[nbn];
                    double[] pt = new double[nbn];
                    double[] qMin1 = new double[nbn];
                    for (int i = 0; i < nbn; i++) {
                        double qMin = q[i] * 60;
                        if (i < nfb) r[i] += a[i];
                        po[i] = Math.round(h[no[i]] * 1000.0) / 1000.0;
                        pt[i] = Math.round(h[nt[i]] * 1000.0) / 1000.0;
                        qMin1[i] = Math.round(qMin * 100.0) / 100.0;
                        writer.printf("%3d %4d %4d %7.2f %7.2f %7.3f %9.5f %8.2f\n",
                                i + 1, no[i], nt[i], h[no[i]], h[nt[i]], q[i], r[i], qMin);
                    }
                    netResult.setNo(no);
                    netResult.setNt(nt);
                    netResult.setPo(po);
                    netResult.setPt(pt);
                    netResult.setQ(q);
                    netResult.setR(r);
                    netResult.setQMin(qMin1);
                    break;
                }
            }
        }
        return netResult;
    }

    void choleskySolve(double[][] a, double[] x, double[] rhs, int n) {
        double[] y = new double[n];
        double[][] d = new double[n][n];
        for (int i = 0; i < n; i++)
            System.arraycopy(a[i], 0, d[i], 0, n);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                double sum = d[i][j];
                for (int k = 0; k < j; k++)
                    sum -= d[i][k] * d[j][k];
                if (i == j)
                    d[i][j] = Math.sqrt(sum);
                else
                    d[i][j] = sum / d[j][j];
            }
            for (int j = i + 1; j < n; j++)
                d[i][j] = 0;
        }

        for (int i = 0; i < n; i++) {
            double sum = rhs[i];
            for (int j = 0; j < i; j++)
                sum -= d[i][j] * y[j];
            y[i] = sum / d[i][i];
        }
        for (int i = n - 1; i >= 0; i--) {
            double sum = y[i];
            for (int j = i + 1; j < n; j++)
                sum -= d[j][i] * x[j];
            x[i] = sum / d[i][i];
        }
    }
}
