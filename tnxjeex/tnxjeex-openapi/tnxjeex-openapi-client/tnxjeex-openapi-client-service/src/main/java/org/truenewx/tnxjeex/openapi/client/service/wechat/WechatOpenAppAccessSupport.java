package org.truenewx.tnxjeex.openapi.client.service.wechat;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatUser;

/**
 * 微信开放平台（open.weixin.qq.com）应用访问支持
 *
 * @author jianglei
 */
public abstract class WechatOpenAppAccessSupport extends WechatAppAccessSupport {

    @Override
    public WechatUser getUser(String loginCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("appid", getAppId());
        params.put("secret", getSecret());
        params.put("code", loginCode);
        params.put("grant_type", "authorization_code");
        Map<String, Object> result = get("/sns/oauth2/access_token", params);
        String openId = (String) result.get("openid");
        if (StringUtils.isNotBlank(openId)) { // openId不能为空
            WechatUser user = new WechatUser();
            user.setAppType(getAppType());
            user.setOpenId(openId);
            user.setUnionId((String) result.get("unionid"));
            user.setAccessToken((String) result.get("access_token"));
            return user;
        }
        return null;
    }

}
