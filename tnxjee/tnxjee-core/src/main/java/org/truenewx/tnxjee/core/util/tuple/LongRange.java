package org.truenewx.tnxjee.core.util.tuple;

/**
 * 长整型范围
 */
public class LongRange extends NumberRange<Long> {

    public static LongRange parse(String s) {
        return (LongRange) parse(s, Long.class, LongRange::new);
    }

    public long getLength() {
        long length = -1;
        Long min = getMin();
        Long max = getMax();
        if (min != null && max != null) {
            length = max - min;
            if (isInclusiveMin() && isInclusiveMax()) { // 两者均包含，则长度需加1
                length++;
            } else if (!isInclusiveMin() && !isInclusiveMax()) { // 两者均不包含，则长度需减1
                length--;
            }
        }
        return length;
    }

    public long getLength(long maxLength) {
        long length = getLength();
        if (length < 0) {
            length = maxLength;
        } else {
            length = Math.min(maxLength, length);
        }
        return length;
    }

}
