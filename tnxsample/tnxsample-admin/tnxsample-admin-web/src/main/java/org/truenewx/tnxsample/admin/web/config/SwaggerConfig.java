package org.truenewx.tnxsample.admin.web.config;

import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.webmvc.api.swagger.SwaggerConfigurerSupport;
import org.truenewx.tnxsample.admin.web.controller.MainController;

@Configuration
public class SwaggerConfig extends SwaggerConfigurerSupport {

    @Override
    protected String getBasePackage() {
        return MainController.class.getPackageName();
    }

}
