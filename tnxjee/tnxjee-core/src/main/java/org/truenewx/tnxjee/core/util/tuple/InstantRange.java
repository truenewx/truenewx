package org.truenewx.tnxjee.core.util.tuple;

import java.time.Instant;

/**
 * 瞬时范围
 */
public class InstantRange extends TemporalRange<Instant> {

    public static InstantRange parse(String s) {
        return (InstantRange) parse(s, Instant.class, InstantRange::new);
    }

}
