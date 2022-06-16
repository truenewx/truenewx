package org.truenewx.tnxjeex.cas.client.authentication.logout;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.webmvc.security.web.SecurityUrlProvider;
import org.truenewx.tnxjeex.cas.core.authentication.logout.CasLogoutSuccessHandler;

/**
 * CAS客户端登出成功处理器
 */
public class CasClientLogoutSuccessHandler extends CasLogoutSuccessHandler {

    public CasClientLogoutSuccessHandler(SecurityUrlProvider urlProvider) {
        super(urlProvider);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String targetUrlParameter = getTargetUrlParameter();
        String targetUrl = request.getParameter(targetUrlParameter);
        if (StringUtils.isNotBlank(targetUrl)) {
            targetUrl = URLEncoder.encode(targetUrl, StandardCharsets.UTF_8);
            targetUrl = NetUtil.mergeParam(getDefaultTargetUrl(), targetUrlParameter, targetUrl);
            return targetUrl;
        }
        return super.determineTargetUrl(request, response);
    }

}
