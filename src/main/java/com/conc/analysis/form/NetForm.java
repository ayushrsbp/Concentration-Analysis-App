package com.conc.analysis.form;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Scope("prototype")
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
    // private List<Integer> nffo;
    // private List<Integer> nfft;
    // private List<Double> rff;
    // private List<Double> qff;

    private int[] no;
    private int[] nt;
    private double[] r;
    private double[] a;
    private double[] fb;

    // private List<Integer> no;
    // private List<Integer> nt;
    // private List<Double> r;
    // private List<Double> a;
    // private List<Double> fb;
}
