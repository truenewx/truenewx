package org.truenewx.tnxjeex.cas.server.security.authentication.logout;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.web.util.WebUtil;

/**
 * CAS服务端登出成功处理器
 */
public class CasServerLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Override
    @Autowired // 覆写以自动注入
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        super.setRedirectStrategy(redirectStrategy);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String targetUrl = super.determineTargetUrl(request, response);
        if (targetUrl.startsWith(Strings.SLASH)) { // 以/开头为相对当前应用的路径，需转换为绝对路径
            String prefix = WebUtil.getProtocolAndHost(request) + request.getContextPath();
            if (prefix.endsWith(Strings.SLASH)) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }
            targetUrl = prefix + targetUrl;
        }
        Map<String, Object> params = WebUtil.getRequestParameterMap(request);
        targetUrl = NetUtil.removeParams(targetUrl, params.keySet());
        targetUrl = NetUtil.mergeParams(targetUrl, params, StandardCharsets.UTF_8.name());
        return targetUrl;
    }

}
