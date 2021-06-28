package org.truenewx.tnxjee.model.spec.enums;

import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.annotation.EnumValue;

/**
 * 最常见的典型进度枚举类型
 */
@Caption("典型进度")
public enum TypicalProcess {

    @Caption("未开始")
    @EnumValue("N")
    NOT_STARTED,

    @Caption("进行中")
    @EnumValue("P")
    PROCESSING,

    @Caption("已成功")
    @EnumValue("S")
    SUCCEEDED,

    @Caption("已失败")
    @EnumValue("F")
    FAILED;

}
