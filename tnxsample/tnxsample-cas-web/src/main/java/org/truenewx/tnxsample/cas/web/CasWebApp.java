package org.truenewx.tnxsample.cas.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.truenewx.tnxjee.Framework;
import org.truenewx.tnxjeex.cas.server.CasServerModule;

@EnableCaching
@EnableFeignClients
@SpringBootApplication(scanBasePackageClasses = { Framework.class, CasServerModule.class, CasWebApp.class })
public class CasWebApp {

    public static void main(String[] args) {
        SpringApplication.run(CasWebApp.class, args);
    }

}
