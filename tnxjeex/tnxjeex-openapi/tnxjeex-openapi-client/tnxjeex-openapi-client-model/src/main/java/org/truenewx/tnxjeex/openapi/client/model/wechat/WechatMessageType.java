package org.truenewx.tnxjeex.openapi.client.model.wechat;

import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.annotation.EnumValue;

/**
 * 微信开放接口消息类型
 *
 * @author jianglei
 */
public enum WechatMessageType {

    @Caption("文本")
    @EnumValue("TX")
    TEXT,

    @Caption("图片")
    @EnumValue("IM")
    IMAGE,

    @Caption("语音")
    @EnumValue("VI")
    VOICE,

    @Caption("视频")
    @EnumValue("VD")
    VIDEO,

    @Caption("小视频")
    @EnumValue("SV")
    SHORTVIDEO,

    @Caption("地理位置")
    @EnumValue("LC")
    LOCATION,

    @Caption("链接")
    @EnumValue("LK")
    LINK,

    @Caption("事件")
    @EnumValue("EV")
    EVENT;

}
