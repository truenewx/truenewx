package org.truenewx.tnxjee.core.util.tuple;

import java.time.LocalDateTime;

/**
 * 日期时间范围
 */
public class LocalDateTimeRange extends TemporalRange<LocalDateTime> {

    public static LocalDateTimeRange parse(String s) {
        return (LocalDateTimeRange) parse(s, LocalDateTime.class, LocalDateTimeRange::new);
    }

}
