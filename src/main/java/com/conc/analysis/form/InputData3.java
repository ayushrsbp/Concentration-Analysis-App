package com.conc.analysis.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Scope("prototype")
@Component
public class InputData3 {
    private int noOfDuctSegments;
    private double emissionRate;
    private int segmentLength;
    private double frictionFactor;
    private double a;
    private double b;
    private double allowedConcentrationMin;
    private double allowedConcentrationMax;
    private int fanCountMin;
    private int fanCountMax;
    private double[][] ducts;
}
