package org.truenewx.tnxjeex.openapi.client.service.wechat;

import org.springframework.core.Ordered;
import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatMessageType;

/**
 * 微信开放接口消息处理器
 *
 * @author jianglei
 */
public interface WechatMessageHandler extends Ordered, Comparable<WechatMessageHandler> {

    /**
     * @return 侦听的消息类型集合
     */
    WechatMessageType getMessageType();

    @Override
    default int getOrder() {
        return 0;
    }

    @Override
    default int compareTo(WechatMessageHandler other) {
        return Integer.valueOf(getOrder()).compareTo(Integer.valueOf(other.getOrder()));
    }

}
