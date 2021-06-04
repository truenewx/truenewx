package org.truenewx.tnxsample.root.web.config;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.webmvc.view.config.WebViewMvcConfigurerSupport;

@Configuration
public class WebMvcConfig extends WebViewMvcConfigurerSupport {

    @Override
    protected void buildSiteMeshFilter(SiteMeshFilterBuilder builder) {
        builder.addDecoratorPaths("/*", "/public/decorator/default.jsp", "/static/libs.jsp");
    }

}
