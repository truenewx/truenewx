package org.truenewx.tnxjee.core.util.tuple;

import java.time.LocalDate;

/**
 * 日期范围
 */
public class LocalDateRange extends TemporalRange<LocalDate> {

    public static LocalDateRange parse(String s) {
        return (LocalDateRange) parse(s, LocalDate.class, LocalDateRange::new);
    }

}
