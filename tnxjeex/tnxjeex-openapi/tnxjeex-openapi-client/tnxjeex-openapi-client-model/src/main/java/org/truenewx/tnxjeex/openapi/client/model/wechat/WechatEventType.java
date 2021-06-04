package org.truenewx.tnxjeex.openapi.client.model.wechat;

import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.annotation.EnumValue;

/**
 * 微信开放接口事件类型
 *
 * @author jianglei
 */
public enum WechatEventType {

    @Caption("关注")
    @EnumValue("S")
    SUBSCRIBE,

    @Caption("取消关注")
    @EnumValue("U")
    UNSUBSCRIBE,

    @Caption("上报地理位置")
    @EnumValue("L")
    LOCATION,

    @Caption("菜单点击")
    @EnumValue("C")
    CLICK,

    @Caption("链接跳转")
    @EnumValue("V")
    VIEW;

}
