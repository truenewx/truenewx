package org.truenewx.tnxjeex.cas.server.security.authentication.logout;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjeex.cas.core.validation.constant.CasParameterNames;
import org.truenewx.tnxjeex.cas.server.entity.AppTicket;
import org.truenewx.tnxjeex.cas.server.service.CasServiceManager;
import org.truenewx.tnxjeex.cas.server.ticket.CasTicketManager;

/**
 * CAS服务端登出处理器
 */
@Component
public class CasServerLogoutHandler implements LogoutHandler {

    @Autowired
    private CasTicketManager ticketManager;
    @Autowired
    private CasServiceManager serviceManager;
    @Autowired
    private Executor executor;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Collection<AppTicket> appTickets = this.ticketManager.deleteTicketGrantingTicket(request, response);
        if (appTickets.size() > 0) {
            String logoutService = WebUtil.getParameterOrAttribute(request, CasParameterNames.SERVICE);
            for (AppTicket ticket : appTickets) {
                String app = ticket.getApp();
                String service = this.serviceManager.getService(app);
                if (logoutService == null || !logoutService.equals(service)) {
                    String logoutProcessUrl = this.serviceManager.getLogoutProcessUrl(service);
                    if (logoutProcessUrl != null) {
                        this.executor.execute(() -> {
                            Map<String, Object> params = new HashMap<>();
                            params.put("logoutRequest", "<SessionIndex>" + ticket.getId() + "</SessionIndex>");
                            NetUtil.requestByGet(logoutProcessUrl, params, StandardCharsets.UTF_8.name());
                        });
                    }
                }
            }
        }
    }
}
