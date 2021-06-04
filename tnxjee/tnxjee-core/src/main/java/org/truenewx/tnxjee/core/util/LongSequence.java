package org.truenewx.tnxjee.core.util;

/**
 * 长整型序列号
 *
 * @author jianglei
 *
 */
public class LongSequence {

    private long sequence;

    public LongSequence() {
    }

    public LongSequence(final long first) {
        this.sequence = first;
    }

    public synchronized long next() {
        return ++this.sequence;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof LongSequence)) {
            return super.equals(obj);
        }
        final LongSequence ls = (LongSequence) obj;
        return this.sequence == ls.sequence;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(this.sequence).hashCode();
    }

    @Override
    public String toString() {
        return String.valueOf(this.sequence);
    }

}
