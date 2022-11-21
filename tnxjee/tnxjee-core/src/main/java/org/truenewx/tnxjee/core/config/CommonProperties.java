package org.truenewx.tnxjee.core.config;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.core.Strings;

/**
 * Web通用配置属性
 *
 * @author jianglei
 */
@Configuration
@ConfigurationProperties("tnxjee.common")
public class CommonProperties implements InitializingBean {

    private Map<String, AppConfiguration> apps = new LinkedHashMap<>(); // 必须用Map，在Spring占位符表达式中才可以取指定应用的配置
    private boolean gatewayEnabled;
    private String gatewayUri;

    public Map<String, AppConfiguration> getApps() {
        return Collections.unmodifiableMap(this.apps);
    }

    public void setApps(Map<String, AppConfiguration> apps) {
        this.apps = apps;
    }

    public boolean isGatewayEnabled() {
        return this.gatewayEnabled;
    }

    public void setGatewayEnabled(boolean gatewayEnabled) {
        this.gatewayEnabled = gatewayEnabled;
    }

    public String getGatewayUri() {
        return this.gatewayUri;
    }

    public void setGatewayUri(String gatewayUri) {
        this.gatewayUri = gatewayUri;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.gatewayEnabled && StringUtils.isNotBlank(this.gatewayUri)) {
            this.apps.values().forEach(app -> {
                // 应用未配置特殊的网关地址，则用通用网关地址作为应用网关地址
                if (StringUtils.isBlank(app.getGatewayUri())) {
                    app.setGatewayUri(this.gatewayUri);
                }
            });
        }
    }

    public int getAppSize() {
        return this.apps.size();
    }

    public AppConfiguration getApp(String name) {
        return name == null ? null : this.apps.get(name);
    }

    public String findAppName(String url, boolean direct) {
        if (url != null) {
            if (url.startsWith(Strings.LEFT_SQUARE_BRACKET)) {
                int index = url.indexOf(Strings.RIGHT_SQUARE_BRACKET);
                if (index > 0) { // 以[appName]开头的地址，检查应用是否存在且允许任意地址
                    String appName = url.substring(1, index);
                    AppConfiguration configuration = getApp(appName);
                    if (configuration != null) {
                        String contextUri = configuration.getContextUri(direct);
                        if (Strings.ASTERISK.equals(contextUri)) {
                            return appName;
                        }
                    }
                    return null; // 不存在或不允许任意地址的，返回null表示未找到
                }
            }
            for (Map.Entry<String, AppConfiguration> entry : this.apps.entrySet()) {
                AppConfiguration configuration = entry.getValue();
                String contextUri = configuration.getContextUri(direct);
                if (url.equals(contextUri) || url.startsWith(contextUri + Strings.SLASH)
                        || url.startsWith(contextUri + Strings.WELL) || url.startsWith(contextUri + Strings.QUESTION)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * 获取所有应用URI，提供给cors配置，作为允许跨域访问的地址清单
     *
     * @return 所有应用URI
     */
    public Set<String> getAllAppUris() {
        Set<String> uris = new HashSet<>();
        this.apps.forEach((name, app) -> {
            if (StringUtils.isNotBlank(app.getGatewayUri())) {
                uris.add(app.getGatewayUri());
            }
            if (StringUtils.isNotBlank(app.getDirectUri())) {
                uris.add(app.getDirectUri());
            }
        });
        return uris;
    }

    public Map<String, String> getAppContextUriMapping() {
        Map<String, String> urls = new HashMap<>();
        this.apps.forEach((name, app) -> {
            urls.put(name, app.getContextUri(false));
        });
        return urls;
    }

    public AppFacade getAppFacade(String name, boolean relativeContextUri) {
        AppConfiguration appConfig = getApp(name);
        if (appConfig == null) {
            return null;
        }
        AppFacade facade = new AppFacade();
        facade.setName(name);
        facade.setSymbol(appConfig.getSymbol());
        facade.setCaption(appConfig.getCaption());
        facade.setBusiness(appConfig.getBusiness());
        if (relativeContextUri) {
            facade.setContextUri(appConfig.getContextPath());
        } else {
            facade.setContextUri(appConfig.getContextUri(false));
        }
        String contextUri = facade.getContextUri();
        if (contextUri != null) {
            facade.setLoginedUri(contextUri + appConfig.getLoginedPath());
        }
        return facade;
    }

}
