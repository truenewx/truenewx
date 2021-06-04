package org.truenewx.tnxjeex.openapi.client.service.wechat;

import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatAppType;

/**
 * 微信Web应用访问器
 */
public abstract class WechatWebAccessor extends WechatOpenAppAccessSupport {

    @Override
    public final WechatAppType getAppType() {
        return WechatAppType.WEB;
    }

}
