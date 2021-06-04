package org.truenewx.tnxsample.cas.web.config;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjeex.cas.server.config.CasServerMvcConfigurerSupport;

@Configuration
public class WebMvcConfig extends CasServerMvcConfigurerSupport {

    @Override
    protected void buildSiteMeshFilter(SiteMeshFilterBuilder builder) {
        super.buildSiteMeshFilter(builder);
        builder.addDecoratorPaths("/*", "/public/decorator/default.jsp", "/static/libs.jsp");
    }

}
