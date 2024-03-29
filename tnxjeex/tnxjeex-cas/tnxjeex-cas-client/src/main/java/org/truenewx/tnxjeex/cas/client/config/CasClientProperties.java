package org.truenewx.tnxjeex.cas.client.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.cas.ServiceProperties;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.config.AppConfiguration;
import org.truenewx.tnxjee.core.config.AppConstants;
import org.truenewx.tnxjee.core.config.CommonProperties;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjeex.cas.core.util.CasUtil;

/**
 * CAS客户端配置属性
 *
 * @author jianglei
 */
@Configuration
@ConfigurationProperties("tnxjeex.cas")
public class CasClientProperties extends ServiceProperties {

    private String serverAppName = "cas";
    @Value(AppConstants.EL_SPRING_APP_NAME)
    private String appName;
    @Autowired
    private CommonProperties commonProperties;

    public String getServerAppName() {
        return this.serverAppName;
    }

    /**
     * @param serverAppName CAS服务器应用名称，默认为cas
     */
    public void setServerAppName(String serverAppName) {
        this.serverAppName = serverAppName;
    }

    @Override
    public void afterPropertiesSet() {
        if (StringUtils.isBlank(getService())) {
            String service;
            AppConfiguration app = this.commonProperties.getApp(this.appName);
            if (app == null) {
                // 属性配置中不包含当前应用的配置，则当前应用可将任意地址作为service，只是需添加特殊前缀
                // 如果需要在属性配置中包含当前应用配置，同时要将任意地址作为service，请配置属性：tnxjeex.cas.service=[appName]
                service = CasUtil.getServicePrefixByAppName(this.appName);
                LogUtil.warn(getClass(),
                        "There is no app named '{}' in tnxjee.common.apps. '{}' has been used as the service",
                        this.appName, service);
            } else {
                service = app.getContextUri(false) + app.getLoginedPath();
            }
            setService(service);
        }
        super.afterPropertiesSet();
    }

    public String getServerContextUri(boolean direct) {
        AppConfiguration app = this.commonProperties.getApp(getServerAppName());
        return app == null ? null : app.getContextUri(direct);
    }

    public String getLoginFormUrl() {
        String url = getServerContextUrl();
        String service = NetUtil.encode(getService());
        return url + "/login?" + getServiceParameter() + Strings.EQUAL + service;
    }

    private String getServerContextUrl() {
        String url = Strings.EMPTY;
        if (!this.appName.equals(this.serverAppName)) { // 当前应用并不同时也是CAS服务端，才需要添加服务端上下文根
            url = getServerContextUri(false);
            if (url.endsWith(Strings.SLASH)) { // 去掉末尾的斜杠
                url = url.substring(0, url.length() - 1);
            }
        }
        return url;
    }

    public String getLogoutSuccessUrl() {
        if (this.appName.equals(this.serverAppName)) { // 当前应用同时也是CAS服务端，则登出成功后默认跳转到登录表单页
            return getLoginFormUrl();
        } else { // 否则当前客户端登出后，跳转到服务端执行登出
            String service = NetUtil.encode(getService());
            return getServerContextUrl() + "/logout?" + getServiceParameter() + Strings.EQUAL + service;
        }
    }

}
