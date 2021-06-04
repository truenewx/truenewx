package org.truenewx.tnxjeex.cas.server.repo;

import java.util.Collection;
import java.util.Optional;

import org.truenewx.tnxjeex.cas.server.entity.AppTicket;

/**
 * 应用票据仓库
 */
public interface AppTicketRepo {

    void save(AppTicket unity);

    Optional<AppTicket> findById(String id);

    AppTicket findByTicketGrantingTicketIdAndApp(String ticketGrantingTicketId, String app);

    Collection<AppTicket> deleteByTicketGrantingTicketId(String ticketGrantingTicketId);

}
