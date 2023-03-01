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
                if (appTicket != null && appTicket.getAppName().equals(app)) {
                    return appTicket;
                }
            }
        }
        return null;
    }

    @Override
    public List<AppTicket> deleteByTicketGrantingTicketIdAndAppNot(String ticketGrantingTicketId, String appNot) {
        List<AppTicket> result = new ArrayList<>();
        synchronized (this.ticketIdMapping) {
            // 先移除，如果存在需要排除的应用，则再加回去
            Set<String> ids = this.ticketIdMapping.remove(ticketGrantingTicketId);
            if (ids != null) {
                // 用迭代器遍历，以便于遍历的同时移除元素
                Iterator<String> idIterator = ids.iterator();
                while (idIterator.hasNext()) {
                    String id = idIterator.next();
                    // 先移除，如果是需要排除的应用，则再加回去
                    AppTicket appTicket = this.dataMapping.remove(id);
                    if (appTicket != null) {
                        if (appTicket.getAppName().equals(appNot)) {
                            this.dataMapping.put(id, appTicket);
                        } else {
                            idIterator.remove();
                            result.add(appTicket);
                        }
                    } else {
                        idIterator.remove();
                    }
                }
                if (ids.size() > 0) {
                    this.ticketIdMapping.put(ticketGrantingTicketId, ids);
                }
            }
        }
        return result;
    }

}
