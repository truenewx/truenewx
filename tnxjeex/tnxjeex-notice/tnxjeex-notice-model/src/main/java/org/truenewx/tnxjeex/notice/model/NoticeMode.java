package org.truenewx.tnxjeex.notice.model;

import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.annotation.EnumValue;

@Caption("通知方式")
public enum NoticeMode {

    @Caption("短信通知")
    @EnumValue("S")
    SMS,

    @Caption("邮件通知")
    @EnumValue("E")
    EMAIL,

    @Caption("消息推送")
    @EnumValue("P")
    PUSH;

}
