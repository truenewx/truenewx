package org.truenewx.tnxjeex.doc.word.config;

import org.jodconverter.core.office.InstalledOfficeManagerHolder;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjeex.doc.word.convert.WordDocConvertProperties;

@Configuration
public class WordDocConfiguration {

    @Autowired
    private WordDocConvertProperties properties;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public OfficeManager officeManager() {
        OfficeManager officeManager = LocalOfficeManager.builder()
                .officeHome(this.properties.getOfficeHome())
                .workingDir(this.properties.getWorkingDir())
                .build();
        InstalledOfficeManagerHolder.setInstance(officeManager);
        return officeManager;
    }

}
