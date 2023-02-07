package org.truenewx.tnxjee.core.config;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.core.util.StringUtil;

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
    private String[] allowedUris;
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

    /**
     * @return 网关地址，用于于浏览器中访问时使用的显示地址，为空时意味着使用直连地址
     */
    public String getGatewayUri() {
        return this.gatewayUri;
    }

    public void setGatewayUri(String gatewayUri) {
        this.gatewayUri = gatewayUri;
    }

    /**
     * @return 直连地址，包含协议、主机地址和可能的端口号的地址
     */
    public String getDirectUri() {
        return this.directUri;
    }

    public void setDirectUri(String directUri) {
        this.directUri = directUri;
    }

    /**
     * @return 除网关地址外，CORS策略中允许的其它地址，支持*表示所有地址
     */
    public String[] getAllowedUris() {
        return this.allowedUris;
    }

    public void setAllowedUris(String[] allowedUris) {
        this.allowedUris = allowedUris;
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

    public String loadSymbol() {
        Assert.hasText(this.symbol, () -> "The app '" + this.caption + "' must specify an symbol");
        return this.symbol;
    }

    /**
     * 获取上下文根路径
     *
     * @param direct 是否直连路径，false-网关路径，如果有的话
     * @return 上下文根路径
     */
    public String getContextUri(boolean direct) {
        // 默认为网关地址，指定需要直连地址或网关地址为空，则使用直连地址
        String uri = this.gatewayUri;
        if (direct || StringUtils.isBlank(uri)) {
            uri = this.directUri;
        }
        // 附加上下文根路径
        return NetUtil.concatUri(uri, this.contextPath);
    }

    public boolean isAllowedUri(String uri) {
        // 判断地址为空，则配置必须允许所有地址
        if (StringUtils.isBlank(uri)) {
            return this.allowedUris != null && ArrayUtils.contains(this.allowedUris, Strings.ASTERISK);
        }
        // 否则需匹配配置的允许地址清单
        String contextUri = NetUtil.getContextUri(uri, this.contextPath);
        return contextUri != null && this.allowedUris != null
                && StringUtil.wildcardMatchOneOf(contextUri, this.allowedUris);
    }

    public String getLoginProcessUrl() {
        return NetUtil.concatUri(getContextUri(false), getLoginPath());
    }

    public String getLogoutProcessUrl() {
        return NetUtil.concatUri(getContextUri(false), getLogoutPath());
    }

}
