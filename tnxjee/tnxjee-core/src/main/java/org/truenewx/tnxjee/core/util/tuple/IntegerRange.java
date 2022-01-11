package org.truenewx.tnxjee.core.util.tuple;

/**
 * 整型范围
 */
public class IntegerRange extends NumberRange<Integer> {

    public static IntegerRange parse(String s) {
        return (IntegerRange) parse(s, Integer.class, IntegerRange::new);
    }

    public Integer getLength() {
        Integer length = null;
        Integer min = getMin();
        Integer max = getMax();
        if (min != null && max != null) {
            length = max - min;
            if (isInclusiveMin() && isInclusiveMax()) { // 两者均包含，则长度需加1
                length++;
            } else if (!isInclusiveMin() && !isInclusiveMax()) { // 两者均不包含，则长度需减1
                length--;
                if (length < 0) {
                    length = 0;
                }
            }
        }
        return length;
    }

    public int getLength(int maxLength) {
        int length = getLength();
        if (length < 0) {
            length = maxLength;
        } else {
            length = Math.min(maxLength, length);
        }
        return length;
    }

}
