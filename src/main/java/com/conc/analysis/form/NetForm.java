package com.conc.analysis.form;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class NetForm {
    private int nff;
    private int nfb;
    private int nn;
    private int iter;
    private double err;

    private int[] nffo;
    private int[] nfft;
    private double[] rff;
    private double[] qff;

    private int[] no;
    private int[] nt;
    private double[] r;
    private double[] a;
    private double[] fb;
}
