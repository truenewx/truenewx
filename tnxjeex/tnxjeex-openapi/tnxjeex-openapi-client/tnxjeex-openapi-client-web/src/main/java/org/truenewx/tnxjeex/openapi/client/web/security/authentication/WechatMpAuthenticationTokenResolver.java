package org.truenewx.tnxjeex.openapi.client.web.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjeex.openapi.client.service.wechat.WechatMpAccessor;

/**
 * 微信小程序登录认证令牌解决器
 *
 * @author jianglei
 */
public class WechatMpAuthenticationTokenResolver extends WechatAuthenticationTokenResolver {

    @Autowired
    private WechatMpAccessor accessor;

    public WechatMpAuthenticationTokenResolver(String loginMode) {
        super(loginMode);
    }

    @Override
    public WechatMpAccessor getAccessor() {
        return this.accessor;
    }

}
