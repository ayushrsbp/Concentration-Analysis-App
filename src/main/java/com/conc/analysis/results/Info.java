package com.conc.analysis.results;

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
public class Info {
    private String os;
    private String version;
    private String architecture;
    private String javaVersion;
    private String user;    
}
