package org.truenewx.tnxjee.model.validation.rule;

/**
 * 范围规则
 *
 * @param <T> 值类型
 * @author jianglei
 */
public abstract class RangeRule<T extends Comparable<T>> extends ValidationRule {

    private T min;
    private T max;
    private boolean inclusiveMin = true;
    private boolean inclusiveMax = true;

    public RangeRule(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return this.min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return this.max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public boolean isInclusiveMin() {
        return this.inclusiveMin;
    }

    public void setInclusiveMin(boolean inclusiveMin) {
        this.inclusiveMin = inclusiveMin;
    }

    public boolean isInclusiveMax() {
        return this.inclusiveMax;
    }

    public void setInclusiveMax(boolean inclusiveMax) {
        this.inclusiveMax = inclusiveMax;
    }

    @Override
    public boolean isValid() {
        return this.min != null && this.max != null && this.min.compareTo(this.max) <= 0;
    }
}
