package org.truenewx.tnxjeex.openapi.client.service.wechat;

import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatMessage;

/**
 * 微信开放接口消息同步处理器
 *
 * @author jianglei
 */
public interface WechatMessageSyncHandler extends WechatMessageHandler {

    /**
     * 当接收到指定类型的消息时触发的同步处理方法，请求会等待本方法返回结果
     *
     * @param message 消息
     * @return 处理完消息后返回给微信服务端的消息，返回null表示没有返回消息
     */
    WechatMessage handleMessage(WechatMessage message);

}
