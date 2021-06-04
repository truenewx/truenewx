package org.truenewx.tnxsample.root.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;
import org.truenewx.tnxjee.Framework;
import org.truenewx.tnxjeex.cas.client.CasClientModule;
import org.truenewx.tnxsample.common.constant.SessionConstants;
import org.truenewx.tnxsample.root.AppRoot;

@EnableCaching
@EnableFeignClients
@EnableMongoHttpSession(maxInactiveIntervalInSeconds = SessionConstants.SESSION_TIMEOUT, collectionName = SessionConstants.SESSION_COLLECTION_NAME)
@SpringBootApplication(scanBasePackageClasses = { Framework.class, CasClientModule.class, AppRoot.class })
public class RootWebApp {

    public static void main(String[] args) {
        SpringApplication.run(RootWebApp.class, args);
    }

}
