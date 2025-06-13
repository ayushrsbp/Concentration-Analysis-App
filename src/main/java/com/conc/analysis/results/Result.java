package com.conc.analysis.results;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Result {
    private double[] concInDuct;
    private double[] concInMine;
    private double concentrationAtFace;
    private double[] intakeFlowRate;
    private double[] returnFlowRate;
}
