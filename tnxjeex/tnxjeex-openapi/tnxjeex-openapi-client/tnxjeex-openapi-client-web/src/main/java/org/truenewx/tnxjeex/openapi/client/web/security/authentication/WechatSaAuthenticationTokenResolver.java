package org.truenewx.tnxjeex.openapi.client.web.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjeex.openapi.client.service.wechat.WechatSaAccessor;

/**
 * 微信公众号登录认证令牌解决器
 *
 * @author jianglei
 */
public class WechatSaAuthenticationTokenResolver extends WechatAuthenticationTokenResolver {

    @Autowired
    private WechatSaAccessor accessor;

    public WechatSaAuthenticationTokenResolver(String loginMode) {
        super(loginMode);
    }

    @Override
    public WechatSaAccessor getAccessor() {
        return this.accessor;
    }

}
