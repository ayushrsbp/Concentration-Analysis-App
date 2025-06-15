package com.conc.analysis.form;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputData {
    private double enterAirFlowRate;
    private MultipartFile file;
    private double emissionRate;
    private int segmentLength;
}
