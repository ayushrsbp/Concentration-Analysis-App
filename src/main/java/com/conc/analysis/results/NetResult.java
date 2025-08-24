package com.conc.analysis.results;

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
public class NetResult {
    private int[] no;
    private int[] nt;
    private double[] po;
    private double[] pt;
    private double[] q;
    private double[] r;
    private double[] qMin;
    private String message;
}
