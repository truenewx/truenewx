package org.truenewx.tnxjeex.cas.server.security.authentication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.view.util.WebViewUtil;
import org.truenewx.tnxjeex.cas.core.validation.constant.CasParameterNames;
import org.truenewx.tnxjeex.cas.server.service.CasServiceManager;
import org.truenewx.tnxjeex.cas.server.ticket.CasTicketManager;

/**
 * CAS鉴权成功处理器
 */
public class CasAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private CasServiceManager serviceManager;
    @Autowired
    private CasTicketManager ticketManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        this.ticketManager.createTicketGrantingTicket(request, response);
        CasUserSpecificDetailsAuthenticationToken token = (CasUserSpecificDetailsAuthenticationToken) authentication;
        String service = token.getService();
        String scope = token.getScope();
        String targetUrl = this.serviceManager.getLoginProcessUrl(request, service, scope);
        Map<String, Object> parameters = WebUtil.getRequestParameterMap(request, "username",
                "password", CasParameterNames.SERVICE, CasParameterNames.SCOPE);
        targetUrl = NetUtil.mergeParams(targetUrl, parameters, StandardCharsets.UTF_8.name());
        // 此处一定是表单提交鉴权成功，无需AjaxRedirectStrategy
        WebViewUtil.redirect(request, response, targetUrl);
    }

}
