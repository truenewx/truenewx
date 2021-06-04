package org.truenewx.tnxjeex.cas.server.entity;

import java.util.Date;

import org.truenewx.tnxjee.model.entity.unity.Unity;

/**
 * 应用票据
 */
public class AppTicket implements Unity<String> {

    private String id;
    private TicketGrantingTicket ticketGrantingTicket;
    private String app;
    private Date createTime;
    private Date expiredTime;

    public AppTicket() {
    }

    public AppTicket(String id) {
        setId(id);
    }

    @Override
    public String getId() {
        return this.id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public TicketGrantingTicket getTicketGrantingTicket() {
        return this.ticketGrantingTicket;
    }

    public void setTicketGrantingTicket(TicketGrantingTicket ticketGrantingTicket) {
        this.ticketGrantingTicket = ticketGrantingTicket;
    }

    public String getApp() {
        return this.app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExpiredTime() {
        return this.expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }
}
