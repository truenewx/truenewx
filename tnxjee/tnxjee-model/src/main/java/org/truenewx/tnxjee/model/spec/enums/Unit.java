package org.truenewx.tnxjee.model.spec.enums;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.annotation.EnumValue;

/**
 * 单位
 *
 * @author jianglei
 * 
 */
public enum Unit {
    @Caption(Strings.EMPTY)
    @EnumValue("NNN")
    NONE(UnitType.NONE, 1),

    @Caption("毫秒")
    @EnumValue("TMS")
    MILLISECOND(UnitType.TIME, 1),

    @Caption("秒")
    @EnumValue("TSC")
    SECOND(UnitType.TIME, 1000),

    @Caption("分钟")
    @EnumValue("TMN")
    MINUTE(UnitType.TIME, 1000 * 60),

    @Caption("小时")
    @EnumValue("THR")
    HOUR(UnitType.TIME, 1000 * 60 * 60),

    @Caption("天")
    @EnumValue("TDY")
    DAY(UnitType.TIME, 1000 * 60 * 60 * 24),

    @Caption("克")
    @EnumValue("WGR")
    GRAM(UnitType.WEIGHT, 1),

    @Caption("千克")
    @EnumValue("WKG")
    KILOGRAM(UnitType.WEIGHT, 1000),

    @Caption("吨")
    @EnumValue("WTN")
    TON(UnitType.WEIGHT, 1000 * 1000),

    @Caption("个")
    @EnumValue("QAN")
    AN(UnitType.QUANTITY, 1),

    @Caption("千")
    @EnumValue("QTH")
    THOUSAND(UnitType.QUANTITY, 1000),

    @Caption("万")
    @EnumValue("QTT")
    TEN_THOUSAND(UnitType.QUANTITY, 10000),

    @Caption("百万")
    @EnumValue("QML")
    MILLION(UnitType.QUANTITY, 1000 * 1000),

    @Caption("亿")
    @EnumValue("QHM")
    HUNDRED_MILLION(UnitType.QUANTITY, 10000 * 10000),

    @Caption("十亿")
    @EnumValue("QBL")
    BILLION(UnitType.QUANTITY, 1000 * 1000 * 1000);

    /**
     * 单位类型
     */
    private UnitType type;
    /**
     * 相对于基准单位的倍数
     */
    private int multiple;

    private Unit(final UnitType type, final int multiple) {
        this.type = type;
        this.multiple = multiple;
    }

    /**
     * @return 单位类型
     */
    public UnitType getType() {
        return this.type;
    }

    /**
     * @return 相对于基准单位的倍数
     */
    public int getMultiple() {
        return this.multiple;
    }

}
