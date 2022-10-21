package org.truenewx.tnxjeex.cas.client.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.security.web.SecurityUrlProvider;
import org.truenewx.tnxjeex.cas.client.config.CasClientProperties;
import org.truenewx.tnxjeex.cas.core.util.CasUtil;

/**
 * Cas客户端的安全地址提供者
 */
@Component
public class CasClientSecurityUrlProvider implements SecurityUrlProvider {

    @Autowired
    private CasClientProperties casClientProperties;
    private Map<String, Object> params = new HashMap<>();

    public void addParam(String name, Object value) {
        this.params.put(name, value);
    }

    public String getServiceParameterName() {
        return this.casClientProperties.getServiceParameter();
    }

    @Override
    public String getDefaultLoginFormUrl() {
        String loginFormUrl = this.casClientProperties.getLoginFormUrl();
        if (this.params.size() > 0) { // 如果存在额外参数，确保原始参数在参数清单中最后
            String parameterString = Strings.EMPTY;
            int index = loginFormUrl.indexOf(Strings.QUESTION);
            if (index >= 0) {
                parameterString = loginFormUrl.substring(index + 1);
                loginFormUrl = loginFormUrl.substring(0, index);
            }
            loginFormUrl = NetUtil.mergeParams(loginFormUrl, this.params, Strings.ENCODING_UTF8)
                    + Strings.AND + parameterString;
        }
        return loginFormUrl;
    }

    @Override
    public String getLoginFormUrl(HttpServletRequest request) {
        String loginFormUrl = getDefaultLoginFormUrl();
        String url = NetUtil.standardizeUrl(request.getRequestURL().toString());
        loginFormUrl = CasUtil.appendService(loginFormUrl, getServiceParameterName(), url);
        if (!WebUtil.isAjaxRequest(request)) {
            String service = this.casClientProperties.getService();
            // 当前访问URL如果以当前service开头但不相等，则将除service外的后缀部分附加到登录表单URL后，以便于登录后跳转到当前访问URL
            if (url.startsWith(service) && !url.equals(service)) {
                loginFormUrl += NetUtil.encode(url.substring(service.length()));
            }
        }
        return loginFormUrl;
    }

    @Override
    public String getLogoutSuccessUrl() {
        return NetUtil.mergeParams(this.casClientProperties.getLogoutSuccessUrl(), this.params, Strings.ENCODING_UTF8);
    }

}
