package org.truenewx.tnxsample.admin.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;
import org.truenewx.tnxjee.Framework;
import org.truenewx.tnxjeex.cas.client.CasClientModule;
import org.truenewx.tnxsample.admin.AppRoot;
import org.truenewx.tnxsample.common.constant.SessionConstants;

@EnableCaching
@EnableFeignClients
@EnableMongoHttpSession(maxInactiveIntervalInSeconds = SessionConstants.SESSION_TIMEOUT, collectionName = SessionConstants.SESSION_COLLECTION_NAME)
@SpringBootApplication(scanBasePackageClasses = { Framework.class, CasClientModule.class, AppRoot.class })
public class AdminWebApp {

    public static void main(String[] args) {
        SpringApplication.run(AdminWebApp.class, args);
    }

}
