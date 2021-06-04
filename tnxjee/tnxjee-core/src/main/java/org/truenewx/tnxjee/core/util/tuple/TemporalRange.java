package org.truenewx.tnxjee.core.util.tuple;

import java.time.temporal.Temporal;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.TemporalUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 时间类范围
 *
 * @author jianglei
 */
public class TemporalRange<T extends Temporal> implements Binate<T, T> {

    private T begin;
    private T end;
    /**
     * 是否包含开始
     */
    private boolean inclusiveBegin = true;
    /**
     * 是否包含结束
     */
    private boolean inclusiveEnd;

    public static <T extends Temporal> TemporalRange<T> parse(String s, Class<T> type) {
        return parse(s, type, TemporalRange::new);
    }

    protected static <T extends Temporal> TemporalRange<T> parse(String s, Class<T> type,
            Supplier<TemporalRange<T>> creator) {
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
            throw new IllegalArgumentException("TemporalRange must start with '[' or '('");
        }

        // 去掉首位的[或(
        s = s.substring(1);
        int index = s.indexOf(Strings.COMMA);
        if (index < 0) {
            throw new IllegalArgumentException("TemporalRange must contain one ','");
        }

        T begin;
        String beginString = s.substring(0, index).trim();
        if (beginString.length() == 0) {
            begin = null;
            inclusiveMin = false; // 没有下限，则一定不包含最小值
        } else {
            begin = TemporalUtil.parse(type, beginString);
        }

        T end;
        String endString = s.substring(index + 1, s.length() - 1).trim();
        if (endString.length() == 0) {
            end = null;
        } else {
            end = TemporalUtil.parse(type, endString);
        }

        boolean inclusiveMax;
        if (s.endsWith(Strings.RIGHT_BRACKET)) {
            inclusiveMax = false;
        } else if (s.endsWith(Strings.RIGHT_SQUARE_BRACKET)) {
            inclusiveMax = true;
        } else {
            throw new IllegalArgumentException("TemporalRange must end with ']' or ')'");
        }
        if (end == null) { // 没有上限，则一定不包含最大值
            inclusiveMax = false;
        }

        TemporalRange<T> range = creator.get();
        range.setBegin(begin);
        range.setEnd(end);
        range.setInclusiveBegin(inclusiveMin);
        range.setInclusiveEnd(inclusiveMax);
        return range;
    }

    public T getBegin() {
        return this.begin;
    }

    public void setBegin(T begin) {
        this.begin = begin;
    }

    public T getEnd() {
        return this.end;
    }

    public void setEnd(T end) {
        this.end = end;
    }

    public boolean isInclusiveBegin() {
        return this.inclusiveBegin;
    }

    public void setInclusiveBegin(boolean inclusiveBegin) {
        this.inclusiveBegin = inclusiveBegin;
    }

    public boolean isInclusiveEnd() {
        return this.inclusiveEnd;
    }

    public void setInclusiveEnd(boolean inclusiveEnd) {
        this.inclusiveEnd = inclusiveEnd;
    }

    @Override
    @JsonIgnore
    public T getLeft() {
        return getBegin();
    }

    @Override
    @JsonIgnore
    public T getRight() {
        return getEnd();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.inclusiveBegin && this.begin != null) {
            sb.append(Strings.LEFT_SQUARE_BRACKET);
        } else {
            sb.append(Strings.LEFT_BRACKET);
        }
        if (this.begin != null) {
            sb.append(TemporalUtil.format(this.begin));
        }
        sb.append(Strings.COMMA);
        if (this.end != null) {
            sb.append(TemporalUtil.format(this.end));
        }

        if (this.inclusiveEnd && this.end != null) {
            sb.append(Strings.RIGHT_SQUARE_BRACKET);
        } else {
            sb.append(Strings.RIGHT_BRACKET);
        }
        return sb.toString();
    }
}
