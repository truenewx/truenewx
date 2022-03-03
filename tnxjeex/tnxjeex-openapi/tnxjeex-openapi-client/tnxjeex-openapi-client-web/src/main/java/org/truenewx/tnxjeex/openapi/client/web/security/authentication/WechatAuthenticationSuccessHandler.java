package org.truenewx.tnxjeex.openapi.client.web.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.truenewx.tnxjee.webmvc.security.web.authentication.AjaxAuthenticationSuccessHandler;

/**
 * 微信授权成功处理器
 *
 * @author jianglei
 */
public class WechatAuthenticationSuccessHandler extends AjaxAuthenticationSuccessHandler {

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        if (isAlwaysUseDefaultTargetUrl()) {
            return getDefaultTargetUrl();
        }
        String targetUrlParameter = getTargetUrlParameter();
        if (targetUrlParameter != null) {
            String targetUrl = (String) request.getAttribute(targetUrlParameter);
            if (StringUtils.isNotBlank(targetUrl)) {
                return targetUrl;
            }
        }
        return super.determineTargetUrl(request, response);
    }

    @Override
    protected Object getAjaxLoginResult(HttpServletRequest request, Authentication authentication) {
        return null;
    }

}
