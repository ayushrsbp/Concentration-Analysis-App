package com.conc.analysis.form;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Scope("prototype")
@Component
public class InputData {
    private double enterAirFlowRate;
    private MultipartFile file;
    private double emissionRate;
    private int segmentLength;
    private double[] leakageFlowRate;
}
