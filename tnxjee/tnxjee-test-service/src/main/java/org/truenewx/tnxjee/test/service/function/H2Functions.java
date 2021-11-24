package org.truenewx.tnxjee.test.service.function;

import java.time.Instant;

import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.core.util.TemporalUtil;

/**
 * H2自定义函数
 */
public class H2Functions {
    // 为适应sql脚本大小写不敏感的特性，方法名一律小写，用下划线分隔单词，不遵循驼峰法规则

    public static String uuid32() {
        return StringUtil.uuid32();
    }

    public static long current_millis() {
        return System.currentTimeMillis();
    }

    public static Long millis(String s) {
        Instant instant = TemporalUtil.parseInstant(s);
        return instant == null ? null : instant.toEpochMilli();
    }

}
