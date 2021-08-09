package org.truenewx.tnxjeex.cas.server.security.authentication.logout;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.config.AppConstants;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjeex.cas.core.constant.CasParameterNames;
import org.truenewx.tnxjeex.cas.server.entity.AppTicket;
import org.truenewx.tnxjeex.cas.server.service.CasServiceManager;
import org.truenewx.tnxjeex.cas.server.ticket.CasTicketManager;

/**
 * CAS服务端登出处理器实现
 */
@Component
public class CasServerLogoutHandlerImpl implements CasServerLogoutHandler {

    @Autowired
    private CasTicketManager ticketManager;
    @Autowired
    private CasServiceManager serviceManager;
    @Value(AppConstants.EL_SPRING_APP_NAME)
    private String appName;
    @Autowired
    private Executor executor;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Collection<AppTicket> appTickets = this.ticketManager.deleteTicketGrantingTicket(request, response);
        if (appTickets.size() > 0) {
            String logoutService = WebUtil.getParameterOrAttribute(request, CasParameterNames.SERVICE);
            for (AppTicket appTicket : appTickets) {
                // 排除当前CAS服务端应用，之所以需要在此排除是考虑到一个应用同时作为CAS服务端和客户端的场景
                if (!appTicket.getApp().equals(this.appName)) {
                    this.executor.execute(() -> {
                        noticeAppLogout(appTicket, logoutService);
                    });
                }
            }
        }
    }

    private void noticeAppLogout(AppTicket appTicket, String excludedService) {
        String appName = appTicket.getApp();
        String logoutProcessUrl = this.serviceManager.getLogoutProcessUrl(appName, excludedService);
        if (logoutProcessUrl != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("logoutRequest", "<SessionIndex>" + appTicket.getId() + "</SessionIndex>");
            NetUtil.requestByGet(logoutProcessUrl, params, StandardCharsets.UTF_8.name());
        }
    }

    @Override
    public void logoutClients(HttpServletRequest request) {
        // 排除当前CAS服务端应用，之所以需要在此排除是考虑到一个应用同时作为CAS服务端和客户端的场景
        List<AppTicket> appTickets = this.ticketManager.deleteAppTickets(request, this.appName);
        for (AppTicket appTicket : appTickets) {
            noticeAppLogout(appTicket, null);
        }
    }

}
