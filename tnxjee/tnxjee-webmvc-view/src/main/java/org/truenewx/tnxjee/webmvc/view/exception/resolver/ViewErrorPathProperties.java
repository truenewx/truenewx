package org.truenewx.tnxjee.webmvc.view.exception.resolver;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("tnxjee.web.view.error.path")
public class ViewErrorPathProperties {

    private String business = "/error/business";
    private String format = "/error/format";

    public String getBusiness() {
        return this.business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
