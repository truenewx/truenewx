package org.truenewx.tnxjeex.doc.word.convert;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("tnxjeex.doc.word.convert")
public class WordDocConvertProperties {

    private String officeHome;
    private String workingDir;

    public String getOfficeHome() {
        return this.officeHome;
    }

    public void setOfficeHome(String officeHome) {
        this.officeHome = officeHome;
    }

    public String getWorkingDir() {
        return this.workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

}
