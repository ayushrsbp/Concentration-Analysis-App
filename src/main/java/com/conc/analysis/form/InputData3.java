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
public class InputData3 {
    private double emissionRate;
    private double a;
    private double b;
    private double[] ductDiameters;
    private double[] frictionFactor;
    private int[] segmentLength;
    private int[] segmentCount;
    private double[] leakageResistance;
}
