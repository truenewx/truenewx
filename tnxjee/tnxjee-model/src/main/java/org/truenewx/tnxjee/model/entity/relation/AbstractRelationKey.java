package org.truenewx.tnxjee.model.entity.relation;

import java.io.Serializable;
import java.util.Objects;

/**
 * 抽象的关系主键模型
 *
 * @param <L> 左键类型
 * @param <R> 右键类型
 */
public abstract class AbstractRelationKey<L extends Serializable, R extends Serializable>
        implements RelationKey<L, R> {

    private static final long serialVersionUID = 3175978730863637299L;

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RelationKey<L, R> other = (RelationKey<L, R>) o;
        return getLeft().equals(other.getLeft()) && getRight().equals(other.getRight());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLeft(), getRight());
    }
}
