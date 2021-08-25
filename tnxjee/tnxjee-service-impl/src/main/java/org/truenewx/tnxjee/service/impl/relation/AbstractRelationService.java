package org.truenewx.tnxjee.service.impl.relation;

import java.io.Serializable;

import org.springframework.util.Assert;
import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxjee.model.entity.relation.Relation;
import org.truenewx.tnxjee.repo.RelationRepox;
import org.truenewx.tnxjee.service.impl.AbstractService;
import org.truenewx.tnxjee.service.relation.CommandRelationService;
import org.truenewx.tnxjee.service.relation.SimpleRelationService;

/**
 * 抽象关系服务
 *
 * @param <T> 关系类型
 * @param <L> 左标识类型
 * @param <R> 右标识类型
 * @author jianglei
 */
public abstract class AbstractRelationService<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
        extends AbstractService<T> implements SimpleRelationService<T, L, R>, CommandRelationService<T, L, R> {

    @Override
    public T find(L leftId, R rightId) {
        RelationRepox<T, L, R> repox = getRepox();
        return repox.find(leftId, rightId);
    }

    @Override
    public T load(L leftId, R rightId) {
        T relation = find(leftId, rightId);
        assertNotNull(relation);
        return relation;
    }

    @Override
    public T add(T relation) {
        T newRelation = beforeSave(null, null, relation);
        Assert.isTrue(newRelation != relation,
                "the returned relation must not be the input relation");
        if (newRelation != null) {
            getRepository().save(newRelation);
            afterSave(newRelation);
        }
        return newRelation;
    }

    @Override
    public T update(L leftId, R rightId, T relation) {
        if (leftId == null || rightId == null) {
            return null;
        }
        T newRelation = beforeSave(leftId, rightId, relation);
        if (newRelation != null) {
            Assert.isTrue(leftId.equals(newRelation.getLeftId()),
                    "leftId must equal relation's leftId");
            Assert.isTrue(rightId.equals(newRelation.getRightId()),
                    "rightId must equal relation's rightId");
            getRepository().save(newRelation);
            afterSave(newRelation);
        }
        return newRelation;
    }

    /**
     * 在保存添加/修改关系前调用，负责验证改动的关系数据，并写入返回的结果关系中<br>
     * <strong>注意：</strong>子类覆写时应确保结果不为null（否则将不保存），且结果关系的标识等于传入的指定标识参数
     *
     * @param leftId   要修改的关系左标识，为null时表示是添加动作
     * @param rightId  要修改的关系右标识，为null时表示是添加动作
     * @param relation 存放添加/修改数据的关系对象
     * @return 已写入数据，即将保存的关系
     */
    protected T beforeSave(L leftId, R rightId, T relation) {
        throw new UnsupportedOperationException();
    }

    /**
     * 关系保存之后调用，负责后续处理
     *
     * @param relation 被保存的关系
     */
    protected void afterSave(T relation) {
    }

    @Override
    public T add(CommandModel<T> commandModel) {
        T relation = beforeSave(null, null, commandModel);
        if (relation != null) {
            getRepository().save(relation);
            afterSave(relation);
        }
        return relation;
    }

    @Override
    public T update(L leftId, R rightId, CommandModel<T> commandModel) {
        if (leftId == null || rightId == null) {
            return null;
        }
        T relation = beforeSave(leftId, rightId, commandModel);
        if (relation != null) {
            Assert.isTrue(leftId.equals(relation.getLeftId()),
                    "leftId must equal relation's leftId");
            Assert.isTrue(rightId.equals(relation.getRightId()),
                    "rightId must equal relation's rightId");
            getRepository().save(relation);
            afterSave(relation);
        }
        return relation;
    }

    /**
     * 在保存添加/修改关系前调用，负责验证改动的模型数据，并写入返回的结果关系中<br>
     * <strong>注意：</strong>子类覆写时应确保结果不为null（否则将不保存），且结果关系的标识等于传入的指定标识参数
     *
     * @param leftId       要修改的关系左标识，为null时表示是添加动作
     * @param rightId      要修改的关系右标识，为null时表示是添加动作
     * @param commandModel 存放添加/修改数据的命令模型
     * @return 已写入数据，即将保存的关系
     */
    protected T beforeSave(L leftId, R rightId, CommandModel<T> commandModel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T delete(L leftId, R rightId) {
        T relation = beforeDelete(leftId, rightId);
        if (relation == null) {
            relation = find(leftId, rightId);
        }
        if (relation != null) {
            getRepository().delete(relation);
            return relation;
        }
        return null;
    }

    /**
     * 根据标识删除关系前调用，由子类覆写<br>
     * 不覆写或子类调用父类的本方法，将无法删除关系
     *
     * @param leftId  要删除的关系左标识，为null时表示是添加动作
     * @param rightId 要删除的关系右标识，为null时表示是添加动作
     * @return 要删除的关系，可返回null，返回非null值有助于提高性能
     */
    protected T beforeDelete(L leftId, R rightId) {
        throw new UnsupportedOperationException();
    }

}
