package org.truenewx.tnxjee.webmvc.view.menu.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * 菜单配置属性
 */
@Configuration
@ConfigurationProperties("tnxjee.web.menu")
public class MenuProperties {

    private String sessionAttributeName;
    private Resource location;

    public String getSessionAttributeName() {
        return this.sessionAttributeName;
    }

    public void setSessionAttributeName(String sessionAttributeName) {
        this.sessionAttributeName = sessionAttributeName;
    }

    public Resource getLocation() {
        return this.location;
    }

    public void setLocation(Resource location) {
        this.location = location;
    }

}
