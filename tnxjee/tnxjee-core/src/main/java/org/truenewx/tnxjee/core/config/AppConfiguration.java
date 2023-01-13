package org.truenewx.tnxjee.core.config;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * 应用配置
 *
 * @author jianglei
 */
public class AppConfiguration {

    private String symbol;
    private String caption;
    private String business;
    private String gatewayUri;
    private String directUri;
    private String contextPath = Strings.EMPTY;
    private String loginPath = "/login/cas";
    private String logoutPath = "/logout";
    private String loginedPath = Strings.EMPTY;
    private Map<String, String> settings;
    private Map<String, String> subs;

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getBusiness() {
        return this.business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getGatewayUri() {
        return this.gatewayUri;
    }

    public void setGatewayUri(String gatewayUri) {
        this.gatewayUri = gatewayUri;
    }

    public String getDirectUri() {
        return this.directUri;
    }

    /**
     * @param directUri 直连地址，包含协议、主机地址和可能的端口号的地址，必须指定
     */
    public void setDirectUri(String directUri) {
        this.directUri = directUri;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    /**
     * @param contextPath 上下文根路径，默认为空字符串
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getLoginPath() {
        return this.loginPath;
    }

    /**
     * @param loginPath 登录的相对路径，默认为/login/cas
     */
    public void setLoginPath(String loginPath) {
        this.loginPath = loginPath;
    }

    public String getLogoutPath() {
        return this.logoutPath;
    }

    /**
     * @param logoutPath 登出的相对路径，默认为/logout
     */
    public void setLogoutPath(String logoutPath) {
        this.logoutPath = logoutPath;
    }

    public String getLoginedPath() {
        return this.loginedPath;
    }

    /**
     * @param loginedPath 登录后的默认跳转相对路径，默认为空字符串
     */
    public void setLoginedPath(String loginedPath) {
        this.loginedPath = loginedPath;
    }

    public Map<String, String> getSettings() {
        return this.settings;
    }

    /**
     * @param settings 附加配置
     */
    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    public Map<String, String> getSubs() {
        return this.subs;
    }

    public void setSubs(Map<String, String> subs) {
        this.subs = subs;
    }

    /**
     * 获取上下文根路径
     *
     * @param direct 是否直连路径，false-网关路径，如果有的话
     * @return 上下文根路径
     */
    public String getContextUri(boolean direct) {
        String directUri = getDirectUri();
        // 如果直连地址为*，则无论需要的是否直连地址，均返回*，表示任意地址，需使用者动态赋值
        if (Strings.ASTERISK.equals(directUri)) {
            return directUri;
        }
        String uri = getGatewayUri();
        // 默认为网关地址，指定需要直连地址或网关地址为空，则使用直连地址
        if (direct || StringUtils.isBlank(uri)) {
            uri = directUri;
        }
        // 添加上下文根
        if (StringUtils.isNotBlank(this.contextPath) && !Strings.SLASH.equals(this.contextPath)) {
            // 确保上下文根以/开头
            if (!this.contextPath.startsWith(Strings.SLASH)) {
                uri += Strings.SLASH;
            }
            uri += this.contextPath;
        }
        return uri;
    }

    public String getLoginProcessUrl() {
        return getContextUri(false) + getLoginPath();
    }

    public String getLogoutProcessUrl() {
        return getContextUri(false) + getLogoutPath();
    }

}
