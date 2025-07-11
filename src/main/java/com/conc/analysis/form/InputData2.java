package com.conc.analysis.form;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Scope("prototype")
@Component
public class InputData2 {
    private int nff;
    private int nfb;
    private int iter;
    private double err;
    private int noOfDuctSegments;
    private double a, b;
    private double ductSegmentResistance, leakageResistance;
    private int[] fanPos;
    private double emissionRate;
    private int segmentLength;
}
