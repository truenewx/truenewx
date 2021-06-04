package org.truenewx.tnxjeex.cas.server.entity;

import java.util.Date;

import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;

/**
 * 票据授权票据
 */
public class TicketGrantingTicket implements Unity<String> {

    private String id;
    // 尽管用户在不同应用上具有不同的操作权限，但是一个操作从一个应用调用到其它应用是大概率事件，所以登录用户必须包含所有应用的操作权限
    private UserSpecificDetails<?> userDetails;
    private Date createTime;
    private Date expiredTime;

    public TicketGrantingTicket() {
    }

    public TicketGrantingTicket(String id) {
        setId(id);
    }

    @Override
    public String getId() {
        return this.id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public UserSpecificDetails<?> getUserDetails() {
        return this.userDetails;
    }

    public void setUserDetails(UserSpecificDetails<?> userDetails) {
        this.userDetails = userDetails;
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
