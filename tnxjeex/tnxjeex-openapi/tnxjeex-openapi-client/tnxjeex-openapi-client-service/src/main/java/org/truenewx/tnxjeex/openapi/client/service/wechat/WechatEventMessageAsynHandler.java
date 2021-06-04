package org.truenewx.tnxjeex.openapi.client.service.wechat;

import org.truenewx.tnxjee.service.transaction.annotation.WriteTransactional;
import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatEventMessage;
import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatEventType;
import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatMessage;
import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatMessageType;

/**
 * 微信开放接口事件消息异步处理器
 *
 * @author jianglei
 */
public abstract class WechatEventMessageAsynHandler implements WechatMessageAsynHandler {

    @Override
    public final WechatMessageType getMessageType() {
        return WechatMessageType.EVENT;
    }

    @Override
    @WriteTransactional
    public void handleMessage(WechatMessage message) {
        WechatEventMessage eventMessage = (WechatEventMessage) message;
        if (eventMessage.getEventType() == getEventType()) {
            doHandleMessage(eventMessage);
        }
    }

    protected abstract WechatEventType getEventType();

    protected abstract void doHandleMessage(WechatEventMessage message);

}
