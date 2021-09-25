package org.truenewx.tnxjee.web.cors;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.core.Strings;

@Configuration
@ConfigurationProperties("tnxjee.web.cors")
public class CorsRegistryProperties {

    private String pathPattern = "/**";

    private String[] allowedOrigins = { Strings.ASTERISK };

    private String[] allowedMethods = { Strings.ASTERISK };

    private String[] allowedHeaders = { Strings.ASTERISK };

    private String[] exposedHeaders = {};

    private Long maxAge;

    public String getPathPattern() {
        return this.pathPattern;
    }

    public void setPathPattern(String pathPattern) {
        this.pathPattern = pathPattern;
    }

    public String[] getAllowedOrigins() {
        return this.allowedOrigins;
    }

    public void setAllowedOrigins(String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public String[] getAllowedMethods() {
        return this.allowedMethods;
    }

    public void setAllowedMethods(String[] allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public String[] getAllowedHeaders() {
        return this.allowedHeaders;
    }

    public void setAllowedHeaders(String[] allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public String[] getExposedHeaders() {
        return this.exposedHeaders;
    }

    public void setExposedHeaders(String[] exposedHeaders) {
        this.exposedHeaders = exposedHeaders;
    }

    public Long getMaxAge() {
        return this.maxAge;
    }

    public void setMaxAge(Long maxAge) {
        this.maxAge = maxAge;
    }

    public boolean isAllAllowed() {
        return ArrayUtils.contains(this.allowedOrigins, Strings.ASTERISK) && ArrayUtils.contains(this.allowedMethods,
                Strings.ASTERISK) && ArrayUtils.contains(this.allowedHeaders, Strings.ASTERISK);
    }

}
