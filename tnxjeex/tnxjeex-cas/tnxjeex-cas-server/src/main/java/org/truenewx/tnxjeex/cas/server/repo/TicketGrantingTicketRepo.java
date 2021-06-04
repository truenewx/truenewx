package org.truenewx.tnxjeex.cas.server.repo;

import java.util.Optional;

import org.truenewx.tnxjeex.cas.server.entity.TicketGrantingTicket;

public interface TicketGrantingTicketRepo {

    void save(TicketGrantingTicket unity);

    Optional<TicketGrantingTicket> findById(String id);

    void delete(TicketGrantingTicket unity);

}
