package org.truenewx.tnxjee.core.util.tuple;

import java.util.Objects;

/**
 * 对称二元体，其左右元顺序不敏感，(a,b)等同于(b,a)
 *
 * @author jianglei
 *
 * @param <T> 元素类型
 */
public class SymplexBinary<T> extends Binary<T, T> {

    /**
     * @param left  左元
     * @param right 右元
     */
    public SymplexBinary(T left, T right) {
        super(left, right);
    }

    /**
     * 反转左右元
     *
     * @author jianglei
     */
    public void reverse() {
        T left = getLeft();
        setLeft(getRight());
        setRight(left);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLeft()) * Objects.hash(getRight());
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SymplexBinary<T> other = (SymplexBinary<T>) obj;
        return (Objects.deepEquals(getLeft(), other.getLeft())
                && Objects.deepEquals(getRight(), other.getRight()))
                || (Objects.deepEquals(getLeft(), other.getRight())
                        && Objects.deepEquals(getRight(), other.getLeft()));
    }

    @Override
    public SymplexBinary<T> clone() {
        return new SymplexBinary<>(getLeft(), getRight());
    }

}
