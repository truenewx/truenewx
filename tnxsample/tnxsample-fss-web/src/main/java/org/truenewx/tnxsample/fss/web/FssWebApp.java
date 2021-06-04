package org.truenewx.tnxsample.fss.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;
import org.truenewx.tnxjee.Framework;
import org.truenewx.tnxjeex.cas.client.CasClientModule;
import org.truenewx.tnxjeex.fss.FssModule;
import org.truenewx.tnxsample.common.constant.SessionConstants;
import org.truenewx.tnxsample.fss.AppRoot;

/**
 * 文件存储服务应用
 *
 * @author jianglei
 */
@EnableCaching
@EnableMongoHttpSession(maxInactiveIntervalInSeconds = SessionConstants.SESSION_TIMEOUT, collectionName = SessionConstants.SESSION_COLLECTION_NAME)
@SpringBootApplication(scanBasePackageClasses = { Framework.class, FssModule.class, CasClientModule.class, AppRoot.class })
public class FssWebApp {

    public static void main(String[] args) {
        SpringApplication.run(FssWebApp.class, args);
    }

}
