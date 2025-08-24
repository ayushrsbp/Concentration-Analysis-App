package com.conc.analysis.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.conc.analysis.form.NetForm;
import com.conc.analysis.results.NetResult;

@Scope("prototype")
@Service
public class NetSolver {

    @Autowired
    private NetResult netResult;

    // ===== Constants reflecting Fortran maxima =====
    private static final int MAX_NFF = 1000; // fixed-flow branches
    private static final int MAX_BRANCH = 1000; // total branches
    private static final int MAX_NODE = 1000; // nodes
    private static final int MAX_KP = 1000; // kp(81) in Fortran
    private static final int MAX_J = 1000000; // ja/jb/in arrays
    private static final int A_LEN = 1000; // a(10)

    // ===== Arrays (0-indexed) =====
    // Fans and pressures
    int[] nffo = new int[MAX_NFF];
    int[] nfft = new int[MAX_NFF];
    double[] rff = new double[MAX_NFF];
    double[] qff = new double[MAX_NFF];
    double[] preg = new double[MAX_NFF];

    // Branch/network arrays
    int[] no = new int[MAX_BRANCH]; // origin node (0-based)
    int[] nt = new int[MAX_BRANCH]; // terminal node (0-based)
    double[] r = new double[MAX_BRANCH];
    double[] c = new double[MAX_BRANCH];
    double[] q = new double[MAX_BRANCH];
    double[] qold = new double[MAX_BRANCH];
    double[] fb = new double[MAX_BRANCH];

    // Node arrays
    double[] xflo = new double[MAX_NODE];

    // CSR-like adjacency helpers
    int[] kp = new int[MAX_KP];
    int[] ja = new int[MAX_J];
    int[] jb = new int[MAX_J];
    int[] in = new int[MAX_J];

    // Misc
    double[] a = new double[A_LEN];

    // COMMON /march/: b(80,80), h(80), rhs(80)
    double[][] b = new double[MAX_NODE][MAX_NODE];
    double[] h = new double[MAX_NODE];
    double[] rhs = new double[MAX_NODE];

    // ===== Scalars =====
    int nff, nfb, nn, iter;
    double err;
    int nbn; // number of branches

    public NetResult solve(NetForm input) {

        nff = input.getNff();
        nfb = input.getNfb();
        nn = input.getNn();
        iter = input.getIter();
        err = input.getErr();

        for (int i = 0; i < nn; i++) {
            h[i] = 0.0;
            xflo[i] = 0.0;
        }

        if (nff != 0) {
            int[] temp_nffo = input.getNffo();
            int[] temp_nfft = input.getNfft();
            double[] temp_rff = input.getRff();
            double[] temp_qff = input.getQff();
            for (int i = 0; i < nff; i++) {
                nffo[i] = temp_nffo[i] - 1;
                nfft[i] = temp_nfft[i] - 1;
                rff[i] = temp_rff[i];
                qff[i] = temp_qff[i];
                xflo[nffo[i]] -= qff[i];
                xflo[nfft[i]] += qff[i];
            }
        }
        int[] temp_no = input.getNo();
        int[] temp_nt = input.getNt();
        double[] temp_r = input.getR();
        double[] temp_a = input.getA();
        double[] temp_fb = input.getFb();

        if (nfb != 0) {
            for (int i = 0; i < nfb; i++) {
                no[i] = temp_no[i] - 1;
                nt[i] = temp_nt[i] - 1;
                double r1 = temp_r[i];
                a[i] = temp_a[i];
                fb[i] = temp_fb[i];
                r[i] = r1 - a[i];
                qold[i] = 0.0;
            }
        }

        // Current branch count starts at nfb
        nbn = nfb;
        while (nbn < temp_no.length) {
            no[nbn] = temp_no[nbn] - 1;
            nt[nbn] = temp_nt[nbn] - 1;
            r[nbn] = temp_r[nbn];
            fb[nbn] = 0;
            qold[nbn] = 0;
            nbn++;
        }

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
        // --- Iterative solve ---
        boolean converged = false;
        for (int itr = 1; itr <= iter; itr++) {
            if (itr == 1) {
                for (int j = 0; j < nbn; j++) {
                    c[j] = 1.0 / r[j];
                }
            } else if (itr == 2) {
                for (int j = 0; j < nbn; j++) {
                    double qab = Math.abs(q[j]);
                    qold[j] = q[j];
                    c[j] = 1.0 / (qab * r[j]);
                }
            } else {
                for (int j = 0; j < nbn; j++) {
                    double qa = Math.sqrt(Math.abs(qold[j] * q[j]));
                    c[j] = 1.0 / (qa * r[j]);
                    double denom = Math.abs(q[j]);
                    qold[j] = (denom == 0.0) ? 0.0 : (qa * q[j] / denom);
                }
            }

// Assemble matrix b and rhs
            for (int i = 0; i < nn; i++) {
                double sm = 0.0;
                double su = 0.0;
// zero row i
                Arrays.fill(b[i], 0, nn, 0.0);

                int kpa = kp[i];
                int kpb = kp[i + 1] - 1;
                for (int m = kpa; m <= kpb; m++) {
                    int br = in[m];
                    sm += c[br];
                    su += jb[m] * c[br] * fb[br];
                    b[i][ja[m]] = -c[br];
                }
                rhs[i] = xflo[i] - su;
                b[i][i] = sm;
            }

// Boundary condition (node 1 in Fortran → index 0)
            b[0][0] = 1.0;
            rhs[0] = 0.0;
            for (int j = 1; j < nn; j++) {
                b[0][j] = 0.0;
                b[j][0] = 0.0;
            }

// Solve b * h = rhs via Cholesky
            choles(b, h, rhs, nn);

// Update flows
            for (int l = 0; l < nbn; l++) {
                q[l] = c[l] * ((h[no[l]] - h[nt[l]]) + fb[l]);
            }

// Convergence check
            boolean ok = true;
            for (int j = 0; j < nbn; j++) {
                if (Math.abs(qold[j] - q[j]) > err) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                converged = true;
                // Output solution and break like Fortran's STOP
                netResult.setMessage("Solution obtained at iteration " + (itr + 1));

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
                    resNo[i] = no[i]+1;
                    resNt[i] = nt[i]+1;
                    resR[i] = Math.round(r[i] * 100000)/100000.0;
                    resQ[i] = Math.round(q[i] * 100)/100.0;
                    resPo[i] = Math.round(h[no[i]] * 10)/10.0;
                    resPt[i] = Math.round(h[nt[i]] * 10)/10.0;
                    resQmin[i] = Math.round(qMin * 10)/10.0;
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
        if (!converged) {
            netResult.setMessage("iterations exceed " +iter+"");
        }
        return netResult;   
    }

    

    public void choles(double[][] b, double[] h, double[] rhs, int n) {
        double[][] d = b; // in-place factorization
        double[] x = h;
        double[] z = rhs;
        double[] y = new double[n];

        Arrays.fill(x, 0, n, 0.0);
        Arrays.fill(y, 0, n, 0.0);

        d[0][0] = Math.sqrt(d[0][0]);
        for (int j = 1; j < n; j++) {
            d[j][0] = d[j][0] / d[0][0];
        }
        for (int i = 1; i <= n - 2; i++) {
            double sum = 0.0;
            for (int k = 0; k <= i - 1; k++) {
                sum += d[i][k] * d[i][k];
            }
            d[i][i] = Math.sqrt(d[i][i] - sum);
            for (int j = i + 1; j < n; j++) {
                sum = 0.0;
                for (int k = 0; k <= i - 1; k++) {
                    sum += d[j][k] * d[i][k];
                }
                d[j][i] = (d[j][i] - sum) / d[i][i];
            }
        }
        double sum = 0.0;
        for (int k = 0; k <= n - 2; k++) {
            sum += d[n - 1][k] * d[n - 1][k];
        }
        d[n - 1][n - 1] = Math.sqrt(d[n - 1][n - 1] - sum);

// Forward substitution
        y[0] = z[0] / d[0][0];
        for (int i = 1; i < n; i++) {
            double s = 0.0;
            for (int j = 0; j <= i - 1; j++) {
                s += d[i][j] * y[j];
            }
            y[i] = (z[i] - s) / d[i][i];
        }
// Back substitution
        x[n - 1] = y[n - 1] / d[n - 1][n - 1];
        for (int i = n - 2; i >= 0; i--) {
            double s = 0.0;
            for (int j = i + 1; j < n; j++) {
                s += d[j][i] * x[j];
            }
            x[i] = (y[i] - s) / d[i][i];
        }
    }
}
