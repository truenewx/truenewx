package org.truenewx.tnxjee.service.relation;

import java.io.Serializable;
import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxjee.model.entity.relation.Relation;

/**
 * 基于命令模型的关系服务
 *
 * @param <T> 关系类型
 * @param <L> 左标识类型
 * @param <R> 右标识类型
 * @author jianglei
 */
public interface CommandRelationService<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
        extends RelationService<T, L, R> {

    /**
     * 添加关系
     *
     * @param commandModel 存放添加数据的命令模型对象
     * @return 添加的关系
     */
    T add(CommandModel<T> commandModel);

    /**
     * 修改关系
     *
     * @param leftId       左标识
     * @param rightId      右标识
     * @param commandModel 存放修改数据的命令模型对象
     * @return 修改后的关系
     */
    T update(L leftId, R rightId, CommandModel<T> commandModel);

}
