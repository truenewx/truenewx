package org.truenewx.tnxjeex.cas.server.repo;

import java.util.*;

import org.truenewx.tnxjeex.cas.server.entity.AppTicket;

/**
 * 内存中的应用票据仓库
 */
public class MemoryAppTicketRepo implements AppTicketRepo {

    private final Map<String, AppTicket> dataMapping = new Hashtable<>(); // appTicketId - appTicket
    private final Map<String, Set<String>> ticketIdMapping = new Hashtable<>(); // ticketGrantingTicketId - appTicketIds

    @Override
    public void save(AppTicket unity) {
        if (unity != null) {
            synchronized (this.ticketIdMapping) {
                String id = unity.getId();
                this.dataMapping.put(id, unity);
                String ticketGrantingTicketId = unity.getTicketGrantingTicket().getId();
                Set<String> ids = this.ticketIdMapping.computeIfAbsent(ticketGrantingTicketId, key -> new HashSet<>());
                ids.add(id);
            }
        }
    }

    @Override
    public Optional<AppTicket> findById(String id) {
        return Optional.ofNullable(this.dataMapping.get(id));
    }

    @Override
    public AppTicket findByTicketGrantingTicketIdAndApp(String ticketGrantingTicketId, String app) {
        Set<String> ids = this.ticketIdMapping.get(ticketGrantingTicketId);
        if (ids != null) {
            for (String id : ids) {
                AppTicket appTicket = this.dataMapping.get(id);
                if (appTicket != null && appTicket.getApp().equals(app)) {
                    return appTicket;
                }
            }
        }
        return null;
    }

    @Override
    public Collection<AppTicket> deleteByTicketGrantingTicketId(String ticketGrantingTicketId) {
        Collection<AppTicket> result = new ArrayList<>();
        synchronized (this.ticketIdMapping) {
            Set<String> ids = this.ticketIdMapping.remove(ticketGrantingTicketId);
            if (ids != null) {
                for (String id : ids) {
                    AppTicket appTicket = this.dataMapping.remove(id);
                    if (appTicket != null) {
                        result.add(appTicket);
                    }
                }
            }
        }
        return result;
    }

}
