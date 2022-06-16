package org.truenewx.tnxjeex.cas.server.authentication.logout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.security.web.SecurityUrlProvider;
import org.truenewx.tnxjeex.cas.core.authentication.logout.CasLogoutSuccessHandler;
import org.truenewx.tnxjeex.cas.core.constant.CasParameterNames;

/**
 * CAS服务端登出成功处理器
 */
public class CasServerLogoutSuccessHandler extends CasLogoutSuccessHandler {

    public CasServerLogoutSuccessHandler(SecurityUrlProvider urlProvider) {
        super(urlProvider);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String targetUrl = request.getParameter(getTargetUrlParameter());
        if (StringUtils.isBlank(targetUrl)) {
            targetUrl = request.getParameter(CasParameterNames.SERVICE);
        }
        if (StringUtils.isBlank(targetUrl)) {
            targetUrl = getDefaultTargetUrl();
        }
        // 以/开头为相对当前应用的路径，需转换为绝对路径
        if (targetUrl.startsWith(Strings.SLASH)) {
            String prefix = WebUtil.getProtocolAndHost(request) + request.getContextPath();
            if (prefix.endsWith(Strings.SLASH)) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }
            targetUrl = prefix + targetUrl;
        }
        return targetUrl;
    }

}
