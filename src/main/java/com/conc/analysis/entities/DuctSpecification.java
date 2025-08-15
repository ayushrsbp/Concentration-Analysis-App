package com.conc.analysis.entities;


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
public class DuctSpecification {
    private double ductDiameters;
    private double frictionFactor;
    private int segmentLength;
    private int segmentCount ;
    private double leakageResistance;
}
