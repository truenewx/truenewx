package org.truenewx.tnxjeex.cas.server.config;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.truenewx.tnxjee.webmvc.view.config.WebViewMvcConfigurerSupport;

/**
 * Cas服务端MVC配置器支持
 */
public class CasServerMvcConfigurerSupport extends WebViewMvcConfigurerSupport {

    @Override
    protected void buildSiteMeshFilter(SiteMeshFilterBuilder builder) {
        builder.addExcludedPath("/serviceValidate");
        builder.addExcludedPath("/serviceLogoutUrls");
    }

}
