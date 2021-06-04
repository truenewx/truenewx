package org.truenewx.tnxjee.core.util.tuple;

/**
 * 长整型范围
 */
public class LongRange extends NumberRange<Long> {

    public static LongRange parse(String s) {
        return (LongRange) parse(s, Long.class, LongRange::new);
    }

}
