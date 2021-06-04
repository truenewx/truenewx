package org.truenewx.tnxjee.core.util.tuple;

/**
 * 整型范围
 */
public class IntegerRange extends NumberRange<Integer> {

    public static IntegerRange parse(String s) {
        return (IntegerRange) parse(s, Integer.class, IntegerRange::new);
    }

}
