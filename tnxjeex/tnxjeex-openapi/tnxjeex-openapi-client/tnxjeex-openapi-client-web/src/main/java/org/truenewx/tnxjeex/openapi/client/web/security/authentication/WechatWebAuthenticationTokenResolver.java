package org.truenewx.tnxjeex.openapi.client.web.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjeex.openapi.client.service.wechat.WechatWebAccessor;

/**
 * 微信网页登录认证令牌解决器
 *
 * @author jianglei
 */
public class WechatWebAuthenticationTokenResolver extends WechatAuthenticationTokenResolver {

    @Autowired
    private WechatWebAccessor accessor;

    public WechatWebAuthenticationTokenResolver(String loginMode) {
        super(loginMode);
    }

    @Override
    public WechatWebAccessor getAccessor() {
        return this.accessor;
    }

}
