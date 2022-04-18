package org.truenewx.tnxjeex.cas.server.ticket;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.validation.Assertion;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.service.Service;
import org.truenewx.tnxjeex.cas.server.entity.AppTicket;

/**
 * CAS票据管理器
 */
public interface CasTicketManager extends Service {

    String TICKET_GRANTING_TICKET_PREFIX = "TGT-";
    String SERVICE_TICKET_PREFIX = "ST-";

    void createTicketGrantingTicket(HttpServletRequest request, HttpServletResponse response);

    boolean checkTicketGrantingTicket(HttpServletRequest request);

    String getAppTicketId(HttpServletRequest request, String app, String scope);

    UserSpecificDetails<?> getUserDetailsInTicketGrantingTicket(HttpServletRequest request);

    List<AppTicket> deleteAppTickets(HttpServletRequest request, String excludedApp);

    List<AppTicket> deleteTicketGrantingTicket(HttpServletRequest request, HttpServletResponse response);

    Assertion validateAppTicket(String app, String appTicketId);
}
