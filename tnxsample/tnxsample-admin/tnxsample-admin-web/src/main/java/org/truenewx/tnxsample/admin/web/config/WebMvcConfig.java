package org.truenewx.tnxsample.admin.web.config;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.webmvc.view.config.WebViewMvcConfigurerSupport;

@Configuration
public class WebMvcConfig extends WebViewMvcConfigurerSupport {
    @Override
    protected void buildSiteMeshFilter(SiteMeshFilterBuilder builder) {
        super.buildSiteMeshFilter(builder);
        builder.addDecoratorPaths("/", "/static/index.jsp");
    }
}
