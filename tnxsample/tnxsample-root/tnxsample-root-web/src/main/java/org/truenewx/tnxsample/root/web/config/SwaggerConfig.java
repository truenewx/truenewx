package org.truenewx.tnxsample.root.web.config;

import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.webmvc.api.swagger.SwaggerConfigurerSupport;
import org.truenewx.tnxsample.root.web.controller.MainController;

@Configuration
public class SwaggerConfig extends SwaggerConfigurerSupport {

    @Override
    protected String getBasePackage() {
        return MainController.class.getPackageName();
    }

}
