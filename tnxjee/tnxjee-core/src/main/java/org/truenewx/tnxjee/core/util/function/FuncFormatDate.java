package org.truenewx.tnxjee.core.util.function;

import org.truenewx.tnxjee.core.util.DateUtil;

import java.util.Date;
import java.util.function.Function;

/**
 * 函数：格式化日期
 *
 * @author jianglei
 */
public class FuncFormatDate implements Function<Date, String> {
    /**
     * 长日期格式的日期格式化函数实例
     */
    public static final FuncFormatDate LONG = new FuncFormatDate(DateUtil.LONG_DATE_PATTERN);
    /**
     * 短日期格式的日期格式化函数实例
     */
    public static final FuncFormatDate SHORT = new FuncFormatDate(DateUtil.SHORT_DATE_PATTERN);

    private String pattern;

    /**
     * @param pattern 格式
     */
    public FuncFormatDate(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return this.pattern;
    }

    @Override
    public String apply(Date date) {
        return DateUtil.format(date, this.pattern);
    }

}
