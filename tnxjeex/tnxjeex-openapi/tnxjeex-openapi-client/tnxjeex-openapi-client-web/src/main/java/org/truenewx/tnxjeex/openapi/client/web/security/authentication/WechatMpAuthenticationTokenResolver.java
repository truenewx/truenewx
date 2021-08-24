package org.truenewx.tnxjeex.openapi.client.web.security.authentication;

import javax.servlet.http.HttpServletRequest;

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
    public WechatMpAccessor getAccessor(HttpServletRequest request) {
        return this.accessor;
    }

}
