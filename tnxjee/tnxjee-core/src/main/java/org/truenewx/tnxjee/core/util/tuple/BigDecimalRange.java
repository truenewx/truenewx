package org.truenewx.tnxjee.core.util.tuple;

import java.math.BigDecimal;

/**
 * 大十进制数范围
 */
public class BigDecimalRange extends NumberRange<BigDecimal> {

    public static BigDecimalRange parse(String s) {
        return (BigDecimalRange) parse(s, BigDecimal.class, BigDecimalRange::new);
    }

}
