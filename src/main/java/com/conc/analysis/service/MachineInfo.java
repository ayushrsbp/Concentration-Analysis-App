package com.conc.analysis.service;

import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.context.annotation.Scope;
=======
>>>>>>> b48738a04a5fc2e9630ee9b1440bd0a8aef826b7
import org.springframework.stereotype.Service;

import com.conc.analysis.results.Info;

<<<<<<< HEAD
@Scope("prototype")
=======
>>>>>>> b48738a04a5fc2e9630ee9b1440bd0a8aef826b7
@Service
public class MachineInfo {

    @Autowired
    private Info info;

    public Info getInfo() {
        String os = System.getProperty("os.name");
        String version = System.getProperty("os.version");
        String architecture = System.getProperty("os.arch");
        String javaVersion = System.getProperty("java.version");
        String user = System.getProperty("user.name");
        info.setOs(os);
        info.setVersion(version);
        info.setArchitecture(architecture);
        info.setJavaVersion(javaVersion);
        info.setUser(user);
        return info;
    }
}
