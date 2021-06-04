package org.truenewx.tnxjee.model.spec.enums;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.caption.Caption;

/**
 * 单位类型
 *
 * @author jianglei
 * 
 */
public enum UnitType {

    @Caption(Strings.EMPTY)
    NONE,

    @Caption("时间")
    TIME,

    @Caption("重量")
    WEIGHT,

    @Caption("长度")
    LENGTH,

    @Caption("面积")
    AREA,

    @Caption("体积")
    VOLUME,

    @Caption("数量")
    QUANTITY;

}
