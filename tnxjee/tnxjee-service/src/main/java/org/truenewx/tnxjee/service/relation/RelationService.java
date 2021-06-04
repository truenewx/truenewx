package org.truenewx.tnxjee.service.relation;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.relation.Relation;
import org.truenewx.tnxjee.service.Service;

/**
 * 关系服务
 *
 * @param <T> 关系类型
 * @param <L> 左标识类型
 * @param <R> 右标识类型
 * @author jianglei
 */
public interface RelationService<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
        extends Service {
    /**
     * 根据标识查找关系，如果找不到则返回null
     *
     * @param leftId  左标识
     * @param rightId 右标识
     * @return 关系
     */
    T find(L leftId, R rightId);

    /**
     * 根据标识加载关系，如果找不到则抛出异常
     *
     * @param leftId  左标识
     * @param rightId 右标识
     * @return 关系
     */
    T load(L leftId, R rightId);

    /**
     * 删除关系
     *
     * @param leftId  左标识
     * @param rightId 右标识
     * @return 被删除的关系，如果实际上未删除任何关系，则返回null
     */
    T delete(L leftId, R rightId);
}
