package org.truenewx.tnxjee.model.spec.enums;

import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.annotation.EnumValue;

/**
 * 设备类型
 *
 * @author jianglei
 */
public enum Device {

    @Caption("电脑")
    @EnumValue("C")
    PC,

    @Caption("手机")
    @EnumValue("H")
    PHONE,

    @Caption("平板")
    @EnumValue("D")
    PAD,
}
