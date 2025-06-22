package com.conc.analysis.results;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Scope("prototype")
@Component
public class Result {
    private double[] concInDuct;
    private double[] concInMine;
    private double concentrationAtFace;
    private double[] leakages;
    private double[] intakeFlowRate;
    private double[] returnFlowRate;
    private int[] distance;
    @Value("true")
    private boolean isValid;
    private int fanCount;
    private double emissionRate;
    private double enterAirFlowRate;
    private int ductSegmentCount;
}
