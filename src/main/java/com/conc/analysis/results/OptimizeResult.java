package com.conc.analysis.results;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.conc.analysis.entities.DuctSpecification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

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
}
