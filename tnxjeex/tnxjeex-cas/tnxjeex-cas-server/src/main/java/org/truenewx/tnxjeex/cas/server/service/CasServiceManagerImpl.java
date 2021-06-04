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
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiMetaProperties;
import org.truenewx.tnxjeex.cas.core.validation.constant.CasParameterNames;
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

    private String artifactParameter = CasParameterNames.ARTIFACT;

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
        return app == null ? null : app.getContextUri(false);
    }

    private AppConfiguration loadAppConfigurationByService(String service) {
        String appName = getAppName(service);
        return loadAppConfigurationByName(appName);
    }

    private AppConfiguration loadAppConfigurationByName(String appName) {
        AppConfiguration appConfiguration = this.commonProperties.getApp(appName);
        if (appConfiguration == null) {
            throw new BusinessException(CasServerExceptionCodes.INVALID_SERVICE);
        }
        return appConfiguration;
    }

    @Override
    public String getUri(HttpServletRequest request, String service) {
        return loadAppConfigurationByService(service).getDirectUri();
    }

    @Override
    public String getLoginProcessUrl(HttpServletRequest request, String service, String scope) {
        String appName = getAppName(service);
        AppConfiguration app = loadAppConfigurationByName(appName);
        String loginUrl = app.getLoginProcessUrl();
        int index = loginUrl.indexOf(Strings.QUESTION);
        if (index < 0) {
            loginUrl += Strings.QUESTION;
        } else {
            loginUrl += Strings.AND;
        }
        loginUrl += this.artifactParameter + Strings.EQUAL + this.ticketManager.getAppTicketId(request, appName, scope);
        String redirectParameter = this.apiMetaProperties.getLoginSuccessRedirectParameter();
        if (StringUtils.isBlank(request.getParameter(redirectParameter))) {
            String contextUri = app.getContextUri(false);
            if (service.length() > contextUri.length()) {
                String redirectUrl = service.substring(contextUri.length());
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
    public String getLogoutProcessUrl(String service) {
        return loadAppConfigurationByService(service).getLogoutProcessUrl();
    }

}
