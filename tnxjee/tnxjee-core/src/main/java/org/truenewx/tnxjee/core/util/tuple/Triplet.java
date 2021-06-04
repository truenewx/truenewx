package org.truenewx.tnxjee.core.util.tuple;

import java.util.Objects;

/**
 * 三元体
 *
 * @author jianglei
 * 
 * @param <L> 左元类型
 * @param <M> 中元类型
 * @param <R> 右元类型
 * @see Triple
 */
public class Triplet<L, M, R> extends Binary<L, R> implements Triple<L, M, R> {

    private M middle;

    public Triplet(L left, M middle, R right) {
        super(left, right);
        this.middle = middle;
    }

    @Override
    public M getMiddle() {
        return this.middle;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int code = super.hashCode();
        code += code * prime + (this.middle == null ? 0 : this.middle.hashCode());
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Triple)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Triple<L, M, R> other = (Triple<L, M, R>) obj;
        return Objects.equals(getLeft(), other.getLeft())
                && Objects.equals(this.middle, other.getMiddle())
                && Objects.equals(getRight(), other.getRight());
    }

    @Override
    public Triplet<L, M, R> clone() {
        return new Triplet<>(getLeft(), this.middle, getRight());
    }

    @Override
    public String toString() {
        return "(" + getLeft() + "," + this.middle + "," + getRight() + ")";
    }

}
