package org.truenewx.tnxjee.core.util.tuple;

import java.time.LocalTime;

/**
 * 时间范围
 */
public class LocalTimeRange extends TemporalRange<LocalTime> {

    public static LocalTimeRange parse(String s) {
        return (LocalTimeRange) parse(s, LocalTime.class, LocalTimeRange::new);
    }

}
