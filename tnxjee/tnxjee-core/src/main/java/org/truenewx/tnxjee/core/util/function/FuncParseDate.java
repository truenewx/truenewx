package org.truenewx.tnxjee.core.util.function;

import java.util.Date;
import java.util.function.Function;

import org.truenewx.tnxjee.core.util.DateUtil;

/**
 * 函数：解析字符串为日期
 *
 * @author jianglei
 * 
 */
public class FuncParseDate implements Function<String, Date> {
    /**
     * 长日期格式的日期解析函数实例
     */
    public static final FuncParseDate LONG = new FuncParseDate(DateUtil.LONG_DATE_PATTERN);
    /**
     * 短日期格式的日期解析函数实例
     */
    public static final FuncParseDate SHORT = new FuncParseDate(DateUtil.SHORT_DATE_PATTERN);

    private String pattern;

    /**
     * @param pattern 格式
     */
    public FuncParseDate(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return this.pattern;
    }

    @Override
    public Date apply(String s) {
        return s == null ? null : DateUtil.parse(s.trim(), this.pattern);
    }

}
