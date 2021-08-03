package org.truenewx.tnxjeex.cas.client.web;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.security.web.SecurityUrlProvider;
import org.truenewx.tnxjeex.cas.client.config.CasClientProperties;

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

    @Override
    public String getDefaultLoginFormUrl() {
        return NetUtil.mergeParams(this.casClientProperties.getLoginFormUrl(), this.params, null);
    }

    @Override
    public String getLoginFormUrl(HttpServletRequest request) {
        String loginFormUrl = getDefaultLoginFormUrl();
        if (!WebUtil.isAjaxRequest(request)) {
            String service = this.casClientProperties.getService();
            String url = request.getRequestURL().toString();
            // 当前访问URL如果以当前service开头但不相等，则将除service外的后缀部分附加到登录表单URL后，以便于登录后跳转到当前访问URL
            if (url.startsWith(service) && !url.equals(service)) {
                loginFormUrl += URLEncoder.encode(url.substring(service.length()), StandardCharsets.UTF_8);
            }
        }
        return loginFormUrl;
    }

    @Override
    public String getLogoutSuccessUrl() {
        return NetUtil.mergeParams(this.casClientProperties.getLogoutSuccessUrl(), this.params, null);
    }

}
