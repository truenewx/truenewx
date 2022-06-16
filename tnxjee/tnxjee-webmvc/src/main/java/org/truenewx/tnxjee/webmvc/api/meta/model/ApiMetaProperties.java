package org.truenewx.tnxjee.webmvc.api.meta.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.web.util.WebConstants;

/**
 * API元数据配置属性
 */
@Configuration
@ConfigurationProperties("tnxjee.web.api")
public class ApiMetaProperties {

    private String[] appNames;
    private String redirectTargetUrlParameter = WebConstants.DEFAULT_REDIRECT_TARGET_URL_PARAMETER;

    public String[] getAppNames() {
        return this.appNames;
    }

    /**
     * @param appNames 相关应用的名称集，默认为整个系统所有应用
     */
    public void setAppNames(String[] appNames) {
        this.appNames = appNames;
    }

    public String getRedirectTargetUrlParameter() {
        return this.redirectTargetUrlParameter;
    }

    public void setRedirectTargetUrlParameter(String redirectTargetUrlParameter) {
        this.redirectTargetUrlParameter = redirectTargetUrlParameter;
    }

}
