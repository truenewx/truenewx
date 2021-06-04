package org.truenewx.tnxjeex.openapi.client.model.wechat;

/**
 * 微信用户详情
 *
 * @author jianglei
 */
public class WechatUserDetail extends WechatUser {

    private String headImageUrl;
    private String nickname;
    private Boolean male;
    private String country;
    private String province;
    private String city;

    public String getHeadImageUrl() {
        return this.headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getMale() {
        return this.male;
    }

    public void setMale(Boolean male) {
        this.male = male;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
