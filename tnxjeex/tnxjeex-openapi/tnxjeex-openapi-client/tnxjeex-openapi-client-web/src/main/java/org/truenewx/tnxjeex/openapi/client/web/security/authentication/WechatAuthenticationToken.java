package org.truenewx.tnxjeex.openapi.client.web.security.authentication;

import org.truenewx.tnxjee.webmvc.security.authentication.UnauthenticatedAuthenticationToken;
import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatUser;

/**
 * 微信登录认证令牌
 */
public class WechatAuthenticationToken extends UnauthenticatedAuthenticationToken {

    private static final long serialVersionUID = 9087828963670023544L;

    public WechatAuthenticationToken(WechatUser user) {
        super(user, null);
    }

    public WechatUser getUser() {
        return (WechatUser) getPrincipal();
    }

}
