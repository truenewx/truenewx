package org.truenewx.tnxjee.webmvc.api.meta.model;

import java.util.HashMap;
import java.util.Map;

import org.truenewx.tnxjee.web.util.WebConstants;

/**
 * API上下文环境
 */
public class ApiContext {

    private String baseApp;
    private String loginSuccessRedirectParameter = WebConstants.DEFAULT_LOGIN_SUCCESS_REDIRECT_PARAMETER;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> apps = new HashMap<>();

    public String getBaseApp() {
        return this.baseApp;
    }

    public void setBaseApp(String baseApp) {
        this.baseApp = baseApp;
    }

    public String getLoginSuccessRedirectParameter() {
        return this.loginSuccessRedirectParameter;
    }

    public void setLoginSuccessRedirectParameter(String loginSuccessRedirectParameter) {
        this.loginSuccessRedirectParameter = loginSuccessRedirectParameter;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public Map<String, String> getApps() {
        return this.apps;
    }

}
