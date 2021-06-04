package org.truenewx.tnxjee.webmvc.servlet;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Servlet上下文配置属性集
 */
@Configuration
@ConfigurationProperties("tnxjee.web.servlet.context")
public class ServletContextProperties {

    private Map<String, String> attributes;

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

}
