package org.truenewx.tnxjeex.openapi.client.service.wechat;

import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatMessage;
import org.truenewx.tnxjeex.openapi.client.service.NoSuchMessageHandlerException;

/**
 * 微信开放接口消息侦听器
 *
 * @author jianglei
 */
public interface WechatMessageListener {

    WechatMessage onReceived(WechatMessage message) throws NoSuchMessageHandlerException;

}
