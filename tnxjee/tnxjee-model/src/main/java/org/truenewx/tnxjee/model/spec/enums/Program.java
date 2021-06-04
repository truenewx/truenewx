package org.truenewx.tnxjee.model.spec.enums;

import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.annotation.EnumValue;

/**
 * 程序类型
 *
 * @author jianglei
 */
public enum Program {

    @Caption("网页")
    @EnumValue("W")
    WEB,

    @Caption("原生")
    @EnumValue("N")
    NATIVE,

    @Caption("小程序")
    @EnumValue("M")
    MP;

}
