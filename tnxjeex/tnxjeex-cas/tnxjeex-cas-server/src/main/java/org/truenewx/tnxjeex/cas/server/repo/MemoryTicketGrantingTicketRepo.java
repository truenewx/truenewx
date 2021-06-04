package org.truenewx.tnxjeex.cas.server.repo;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import org.truenewx.tnxjeex.cas.server.entity.TicketGrantingTicket;

/**
 * 内存中的票据授权票据仓库
 */
public class MemoryTicketGrantingTicketRepo implements TicketGrantingTicketRepo {

    private Map<String, TicketGrantingTicket> dataMapping = new Hashtable<>();

    @Override
    public void save(TicketGrantingTicket unity) {
        if (unity != null) {
            this.dataMapping.put(unity.getId(), unity);
        }
    }

    @Override
    public Optional<TicketGrantingTicket> findById(String id) {
        return Optional.ofNullable(this.dataMapping.get(id));
    }

    @Override
    public void delete(TicketGrantingTicket unity) {
        if (unity != null) {
            this.dataMapping.remove(unity.getId());
        }
    }

}
