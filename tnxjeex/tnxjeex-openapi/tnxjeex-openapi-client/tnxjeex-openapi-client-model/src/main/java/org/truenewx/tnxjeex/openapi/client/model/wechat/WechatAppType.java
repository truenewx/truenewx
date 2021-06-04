package org.truenewx.tnxjeex.openapi.client.model.wechat;

import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.annotation.EnumValue;

/**
 * 微信应用类型
 *
 * @author jianglei
 */
public enum WechatAppType {

    @Caption("小程序")
    @EnumValue("M")
    MP,

    @Caption("公众号")
    @EnumValue("S")
    SA,

    @Caption("网站")
    @EnumValue("W")
    WEB;

}
