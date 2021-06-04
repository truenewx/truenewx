package org.truenewx.tnxjeex.openapi.client.model.wechat;

/**
 * 微信开放接口文本消息
 *
 * @author jianglei
 */
public class WechatTextMessage extends WechatMessage {

    private String content;

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public WechatMessageType getType() {
        return WechatMessageType.TEXT;
    }

}
