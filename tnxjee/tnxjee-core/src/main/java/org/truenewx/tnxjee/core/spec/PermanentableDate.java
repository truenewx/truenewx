package org.truenewx.tnxjee.core.spec;

import java.time.LocalDate;
import java.util.Objects;

import org.truenewx.tnxjee.core.util.TemporalUtil;

/**
 * 可表示永久的日期
 *
 * @author jianglei
 */
public class PermanentableDate implements Comparable<PermanentableDate> {

    /**
     * 表示永久的日期值
     */
    public static final LocalDate PERMANENT_DATE = LocalDate.of(9999, 12, 31);

    private LocalDate value;

    public PermanentableDate() {
    }

    public PermanentableDate(LocalDate value) {
        this.value = value;
    }

    public PermanentableDate(String value) {
        this(TemporalUtil.parseDate(value));
    }

    public static PermanentableDate ofPermanent() {
        return new PermanentableDate(PERMANENT_DATE);
    }

    public LocalDate getValue() {
        return this.value;
    }

    public void setValue(LocalDate value) {
        this.value = value;
    }

    public boolean isPermanent() {
        return PERMANENT_DATE.equals(this.value);
    }

    public void setPermanent(boolean permanent) {
        if (permanent) {
            this.value = PERMANENT_DATE;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PermanentableDate that = (PermanentableDate) o;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public String toString() {
        if (isPermanent()) {
            return "permanent";
        }
        return TemporalUtil.format(this.value);
    }

    @Override
    public int compareTo(PermanentableDate other) {
        if (this.value == null) {
            if (other.value == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (other.value == null) {
                return 1;
            } else {
                return this.value.compareTo(other.value);
            }
        }
    }

}
