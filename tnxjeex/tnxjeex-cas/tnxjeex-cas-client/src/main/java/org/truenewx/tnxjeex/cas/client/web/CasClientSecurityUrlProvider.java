package org.truenewx.tnxjeex.cas.client.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
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
        if (!WebUtil.isAjaxRequest(request)) { // 不是AJAX请求，才需要附加当前执行动作
            String action = WebUtil.getRelativeRequestAction(request);
            if (StringUtils.isNotBlank(action) && !action.equals(Strings.SLASH)) {
                loginFormUrl += action;
            }
        }
        return loginFormUrl;
    }

    @Override
    public String getLogoutSuccessUrl() {
        return NetUtil.mergeParams(this.casClientProperties.getLogoutSuccessUrl(), this.params, null);
    }

}
