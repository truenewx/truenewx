package org.truenewx.tnxjeex.fss.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件存储访问器配置属性集
 */
@Configuration
@ConfigurationProperties("tnxjeex.fss.accessor.local")
public class FssLocalAccessorProperties {

    /**
     * 本地访问器根目录
     */
    private String root;

    public String getRoot() {
        return this.root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

}
