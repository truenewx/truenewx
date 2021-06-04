package org.truenewx.tnxjee.core.util.tuple;

import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.MathUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 数值范围
 *
 * @author jianglei
 */
public class NumberRange<T extends Number> implements Binate<T, T> {

    private T min;
    private T max;
    /**
     * 是否包含最小值
     */
    private boolean inclusiveMin = true;
    /**
     * 是否包含最大值
     */
    private boolean inclusiveMax;

    public static <T extends Number> NumberRange<T> parse(String s, Class<T> type) {
        return parse(s, type, NumberRange::new);
    }

    protected static <T extends Number> NumberRange<T> parse(String s, Class<T> type,
            Supplier<NumberRange<T>> creator) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        s = s.trim();

        boolean inclusiveMin;
        if (s.startsWith(Strings.LEFT_SQUARE_BRACKET)) {
            inclusiveMin = true;
        } else if (s.startsWith(Strings.LEFT_BRACKET)) {
            inclusiveMin = false;
        } else {
            throw new IllegalArgumentException("NumberRange must start with '[' or '('");
        }

        // 去掉首位的[或(
        s = s.substring(1);
        int index = s.indexOf(Strings.COMMA);
        if (index < 0) {
            throw new IllegalArgumentException("NumberRange must contain one ','");
        }
        T min;
        String minString = s.substring(0, index).trim();
        if (minString.length() == 0 || "-∞".equals(minString)) {
            min = null;
            inclusiveMin = false; // 没有下限，则一定不包含最小值
        } else {
            min = MathUtil.parse(minString, type);
        }
        T max;
        String maxString = s.substring(index + 1, s.length() - 1).trim();
        if (maxString.length() == 0 || "+∞".equals(maxString)) {
            max = null;
        } else {
            max = MathUtil.parse(maxString, type);
        }

        boolean inclusiveMax;
        if (s.endsWith(Strings.RIGHT_BRACKET)) {
            inclusiveMax = false;
        } else if (s.endsWith(Strings.RIGHT_SQUARE_BRACKET)) {
            inclusiveMax = true;
        } else {
            throw new IllegalArgumentException("NumberRange must end with ']' or ')'");
        }
        if (max == null) { // 没有上限，则一定不包含最大值
            inclusiveMax = false;
        }

        NumberRange<T> range = creator.get();
        range.setMin(min);
        range.setMax(max);
        range.setInclusiveMin(inclusiveMin);
        range.setInclusiveMax(inclusiveMax);
        return range;
    }

    public T getMin() {
        return this.min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return this.max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public boolean isInclusiveMin() {
        return this.inclusiveMin;
    }

    public void setInclusiveMin(boolean inclusiveMin) {
        this.inclusiveMin = inclusiveMin;
    }

    public boolean isInclusiveMax() {
        return this.inclusiveMax;
    }

    public void setInclusiveMax(boolean inclusiveMax) {
        this.inclusiveMax = inclusiveMax;
    }

    @Override
    @JsonIgnore
    public T getLeft() {
        return getMin();
    }

    @Override
    @JsonIgnore
    public T getRight() {
        return getMax();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.inclusiveMin && this.min != null) {
            sb.append(Strings.LEFT_SQUARE_BRACKET);
        } else {
            sb.append(Strings.LEFT_BRACKET);
        }
        if (this.min != null) {
            sb.append(this.min.toString());
        } else { // 无下限，则添加负无穷
            sb.append(Strings.MINUS).append(Strings.INFINITY);
        }
        sb.append(Strings.COMMA);
        if (this.max != null) {
            sb.append(this.max.toString());
        } else { // 无上限，则添加正无穷
            sb.append(Strings.PLUS).append(Strings.INFINITY);
        }

        if (this.inclusiveMax && this.max != null) {
            sb.append(Strings.RIGHT_SQUARE_BRACKET);
        } else {
            sb.append(Strings.RIGHT_BRACKET);
        }
        return sb.toString();
    }
}
