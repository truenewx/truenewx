package org.truenewx.tnxjeex.openapi.client.model.wechat;

/**
 * 微信开放接口事件消息
 *
 * @author jianglei
 */
public class WechatEventMessage extends WechatMessage {

    private WechatEventType eventType;

    public WechatEventMessage(WechatEventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public WechatMessageType getType() {
        return WechatMessageType.EVENT;
    }

    public WechatEventType getEventType() {
        return this.eventType;
    }

}
