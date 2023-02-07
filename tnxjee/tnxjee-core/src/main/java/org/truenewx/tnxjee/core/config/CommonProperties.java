package org.truenewx.tnxjee.core.config;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;

/**
 * Web通用配置属性
 *
 * @author jianglei
 */
@Configuration
@ConfigurationProperties(CommonProperties.PROPERTY_PREFIX)
public class CommonProperties implements InitializingBean {

    public static final String PROPERTY_PREFIX = "tnxjee.common";
    public static final String PROPERTY_APPS_PREFIX = PROPERTY_PREFIX + ".apps.";

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

    public String findAppName(String uri, boolean direct) {
        if (uri != null) {
            if (uri.startsWith(Strings.LEFT_SQUARE_BRACKET)) {
                int index = uri.indexOf(Strings.RIGHT_SQUARE_BRACKET);
                if (index > 0) { // 以[appName]开头的地址，检查应用是否存在且为允许访问地址
                    String appName = uri.substring(1, index);
                    uri = uri.substring(index + 1);
                    uri = NetUtil.standardizeUri(uri);
                    AppConfiguration configuration = getApp(appName);
                    if (configuration != null && configuration.isAllowedUri(uri)) {
                        return appName;
                    }
                    return null;
                }
            }
            uri = NetUtil.standardizeUri(uri);
            for (Map.Entry<String, AppConfiguration> entry : this.apps.entrySet()) {
                AppConfiguration configuration = entry.getValue();
                String contextUri = NetUtil.standardizeUri(configuration.getContextUri(direct));
                if (uri.equals(contextUri) || uri.startsWith(contextUri + Strings.SLASH)
                        || uri.startsWith(contextUri + Strings.WELL) || uri.startsWith(contextUri + Strings.QUESTION)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * 允许cors跨域访问的地址清单
     *
     * @return 所有应用URI
     */
    public Set<String> getAllAllowedUris() {
        Set<String> uris = new HashSet<>();
        for (AppConfiguration configuration : this.apps.values()) {
            String[] allowedUris = configuration.getAllowedUris();
            if (allowedUris != null) {
                uris.addAll(Arrays.asList(allowedUris));
            }
            if (StringUtils.isNotBlank(configuration.getGatewayUri())) {
                uris.add(configuration.getGatewayUri());
            }
            if (StringUtils.isNotBlank(configuration.getDirectUri())) {
                uris.add(configuration.getDirectUri());
            }
        }
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
        AppConfiguration configuration = getApp(name);
        if (configuration == null) {
            return null;
        }
        AppFacade facade = new AppFacade();
        facade.setName(name);
        facade.setSymbol(configuration.getSymbol());
        facade.setCaption(configuration.getCaption());
        facade.setBusiness(configuration.getBusiness());
        if (relativeContextUri) {
            facade.setContextUri(configuration.getContextPath());
        } else {
            facade.setContextUri(configuration.getContextUri(false));
        }
        String contextUri = facade.getContextUri();
        if (StringUtils.isNotBlank(contextUri)) {
            facade.setLoginedUri(NetUtil.concatUri(contextUri, configuration.getLoginedPath()));
        }
        return facade;
    }

}
