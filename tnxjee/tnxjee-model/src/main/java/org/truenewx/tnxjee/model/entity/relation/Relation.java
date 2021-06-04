package org.truenewx.tnxjee.model.entity.relation;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.Entity;

/**
 * 关系模型
 *
 * @author jianglei
 * @param <L> 左标识类型
 * @param <R> 右标识类型
 */
public interface Relation<L extends Serializable, R extends Serializable> extends Entity {

    /**
     *
     * @return 左标识
     */
    L getLeftId();

    /**
     *
     * @return 右标识
     */
    R getRightId();

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);
}
