package org.truenewx.tnxjeex.openapi.client.service.wechat;

import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatMessage;

/**
 * 微信开放接口消息异步处理器
 *
 * @author jianglei
 */
public interface WechatMessageAsynHandler extends WechatMessageHandler {

    /**
     * 当接收到指定类型的消息时触发的同步处理方法，请求不会等待本方法执行完毕
     *
     * @param message 消息
     */
    void handleMessage(WechatMessage message);

}
