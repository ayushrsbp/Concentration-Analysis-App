package com.conc.analysis.results;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.conc.analysis.entities.DuctSpecification;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Scope("prototype")
@Component
public class OptimizeResult {
    private List<Double>[] concentrations;
    private List<Double>[] airFlowRateAtEntry;
    private List<Double>[] airFlowRateAtFace;
    private DuctSpecification[] ductSpecification;
    private List<Result>[] allResults;
    private Double maxConc;
    private double emissionRate;
    private double a;
    private double b;
}
