package org.truenewx.tnxjeex.openapi.client.model.wechat;

/**
 * 微信用户标识
 *
 * @author jianglei
 */
public class WechatUser {

    private WechatAppType appType;
    private String openId;
    private String unionId;
    private String sessionKey;
    private String accessToken;

    public WechatAppType getAppType() {
        return this.appType;
    }

    public void setAppType(WechatAppType appType) {
        this.appType = appType;
    }

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return this.unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getSessionKey() {
        return this.sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
