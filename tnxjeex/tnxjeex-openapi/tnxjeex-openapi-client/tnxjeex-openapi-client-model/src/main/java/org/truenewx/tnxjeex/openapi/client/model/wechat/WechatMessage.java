package org.truenewx.tnxjeex.openapi.client.model.wechat;

import java.time.Instant;

/**
 * 微信开放接口消息
 *
 * @author jianglei
 */
public abstract class WechatMessage {

    private long id;
    private String fromUsername;
    private String toUsername;
    private Instant createTime;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFromUsername() {
        return this.fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getToUsername() {
        return this.toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public abstract WechatMessageType getType();

}
