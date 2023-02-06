package org.truenewx.tnxjeex.cas.server.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.config.AppConfiguration;
import org.truenewx.tnxjee.core.config.CommonProperties;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiMetaProperties;
import org.truenewx.tnxjeex.cas.core.CasConstants;
import org.truenewx.tnxjeex.cas.core.util.CasUtil;
import org.truenewx.tnxjeex.cas.server.ticket.CasTicketManager;

/**
 * CAS服务管理器实现
 *
 * @author jianglei
 */
@Component
public class CasServiceManagerImpl implements CasServiceManager {

    private static final String ENCODED_WELL = URLEncoder.encode(Strings.WELL, StandardCharsets.UTF_8);

    @Autowired
    private CommonProperties commonProperties;
    @Autowired
    private CasTicketManager ticketManager;
    @Autowired
    private ApiMetaProperties apiMetaProperties;

    private String artifactParameter = CasConstants.PARAMETER_ARTIFACT;

    public void setArtifactParameter(String artifactParameter) {
        this.artifactParameter = artifactParameter;
    }

    @Override
    public String getAppName(String service) {
        return this.commonProperties.findAppName(service, false);
    }

    @Override
    public String getService(String appName) {
        AppConfiguration app = this.commonProperties.getApp(appName);
        return getService(app);
    }

    private String getService(AppConfiguration app) {
        if (app != null) {
            String contextUri = app.getContextUri(false);
            return NetUtil.concatUri(contextUri, app.getLoginedPath());
        }
        return null;
    }

    private AppConfiguration loadAppConfiguration(String appName) {
        AppConfiguration configuration = this.commonProperties.getApp(appName);
        if (configuration == null) {
            throw new BusinessException(CasServerExceptionCodes.INVALID_SERVICE);
        }
        return configuration;
    }

    @Override
    public String getUri(HttpServletRequest request, String service) {
        String appName = getAppName(service);
        return loadAppConfiguration(appName).getDirectUri();
    }

    @Override
    public String getLoginProcessUrl(HttpServletRequest request, String service, String scope) {
        String appName = getAppName(service);
        AppConfiguration app = loadAppConfiguration(appName);
        String contextUrl;
        String loginUrl;
        if (service.startsWith(CasUtil.getServicePrefixByAppName(appName))) {
            service = service.substring(appName.length() + 2);
            String contextPath = app.getContextPath();
            contextUrl = NetUtil.getContextUrl(service, contextPath);
            if (contextUrl == null) {
                throw new BusinessException(CasServerExceptionCodes.INVALID_SERVICE);
            }
            loginUrl = contextUrl + app.getLoginPath();
        } else {
            contextUrl = app.getContextUri(false);
            loginUrl = app.getLoginProcessUrl();
        }
        int index = loginUrl.indexOf(Strings.QUESTION);
        if (index < 0) {
            loginUrl += Strings.QUESTION;
        } else {
            loginUrl += Strings.AND;
        }
        loginUrl += this.artifactParameter + Strings.EQUAL + this.ticketManager.getAppTicketId(request, appName, scope);
        String redirectParameter = this.apiMetaProperties.getRedirectTargetUrlParameter();
        if (StringUtils.isBlank(request.getParameter(redirectParameter))) {
            if (service.length() > contextUrl.length()) {
                String redirectUrl = service.substring(contextUrl.length());
                if (redirectUrl.contains(Strings.WELL)) {
                    redirectUrl = redirectUrl.replace(Strings.WELL, ENCODED_WELL);
                }
                if (!redirectUrl.startsWith(Strings.SLASH)) { // 确保以斜杠开头
                    redirectUrl = Strings.SLASH + redirectUrl;
                }
                loginUrl += Strings.AND + redirectParameter + Strings.EQUAL + redirectUrl;
            }
        }
        return loginUrl;
    }

    @Override
    public String getLogoutProcessUrl(String appName, String serviceNot) {
        AppConfiguration app = loadAppConfiguration(appName);
        if (serviceNot != null) {
            String service = getService(app);
            if (service.equals(serviceNot)) {
                return null;
            }
            if (Strings.ASTERISK.equals(service) && serviceNot.startsWith(CasUtil.getServicePrefixByAppName(appName))) {
                return null;
            }
        }
        return app.getLogoutProcessUrl();
    }

}
