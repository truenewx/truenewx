package org.truenewx.tnxjee.service.impl.unity;

import java.io.Serializable;

import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxjee.model.entity.unity.OwnedUnity;
import org.truenewx.tnxjee.repo.OwnedUnityRepox;
import org.truenewx.tnxjee.service.unity.CommandOwnedUnityService;
import org.truenewx.tnxjee.service.unity.SimpleOwnedUnityService;

/**
 * 抽象的从属单体的服务
 *
 * @param <T> 单体类型
 * @param <K> 标识类型
 * @param <O> 所属者类型
 * @author jianglei
 */
public abstract class AbstractOwnedUnityService<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends AbstractUnityService<T, K>
        implements SimpleOwnedUnityService<T, K, O>, CommandOwnedUnityService<T, K, O> {

    private static final String MESSAGE_OWNER_NOT_EQUAL = "owner should equal unity's owner";

    @Override
    public T find(O owner, K id) {
        OwnedUnityRepox<T, K, O> repox = getRepox();
        return repox.findByOwnerAndId(owner, id);
    }

    @Override
    public T load(O owner, K id) {
        T unity = find(owner, id);
        assertNotNull(unity);
        return unity;
    }

    @Override
    public T add(O owner, T unity) {
        if (owner == null) {
            return null;
        }
        unity = beforeSave(owner, null, unity);
        doAdd(owner, unity);
        return unity;
    }

    private void doAdd(O owner, T unity) {
        if (unity != null) {
            if (!owner.equals(unity.getOwner())) {
                LogUtil.warn(getClass(), MESSAGE_OWNER_NOT_EQUAL);
            }
            getRepository().save(unity);
            afterSave(unity);
        }
    }

    @Override
    public T update(O owner, K id, T unity) {
        if (owner == null || id == null) {
            return null;
        }
        unity = beforeSave(owner, id, unity);
        doUpdate(owner, id, unity);
        return unity;
    }

    private void doUpdate(O owner, K id, T unity) {
        if (unity != null) {
            Assert.isTrue(id.equals(unity.getId()), MESSAGE_ID_NOT_EQUAL);
            if (!owner.equals(unity.getOwner())) {
                LogUtil.warn(getClass(), MESSAGE_OWNER_NOT_EQUAL);
            }
            getRepository().save(unity);
            afterSave(unity);
        }
    }

    /**
     * 在保存添加/修改有所属者的单体前调用，负责验证改动的单体数据，并写入返回的结果单体中<br/>
     * <strong>注意：</strong>子类覆写时应确保结果不为null（否则将不保存），且结果单体的标识等于传入的指定标识参数；<br/>
     * 一般情况下子类不应修改单体的所属者，特殊业务场景下可以，但请务必确保业务逻辑的正确性
     *
     * @param owner 所属者
     * @param id    要修改的单体标识，为null时表示是添加动作
     * @param unity 存放添加/修改数据的单体对象
     * @return 已写入数据，即将保存的单体
     */
    protected T beforeSave(O owner, K id, T unity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T add(O owner, CommandModel<T> commandModel) {
        if (owner == null) {
            return null;
        }
        T unity = beforeSave(owner, null, commandModel);
        doAdd(owner, unity);
        return unity;
    }

    @Override
    public T update(O owner, K id, CommandModel<T> commandModel) {
        if (owner == null || id == null) {
            return null;
        }
        T unity = beforeSave(owner, id, commandModel);
        doUpdate(owner, id, unity);
        return unity;
    }

    /**
     * 在保存添加/修改有所属者的单体前调用，负责验证改动的模型数据，并写入返回的结果单体中<br/>
     * <strong>注意：</strong>子类覆写时应确保结果不为null（否则将不保存），且结果单体的标识等于传入的指定标识参数；<br/>
     * 一般情况下子类不应修改单体的所属者，特殊业务场景下可以，但请务必确保业务逻辑的正确性
     *
     * @param owner        所属者
     * @param id           要修改的单体标识，为null时表示是添加动作
     * @param commandModel 存放添加/修改数据的命令模型
     * @return 已写入数据，即将保存的从属单体
     */
    protected T beforeSave(O owner, K id, CommandModel<T> commandModel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T delete(O owner, K id) {
        if (owner != null && id != null) {
            T unity = beforeDelete(owner, id);
            if (unity == null) {
                unity = find(owner, id);
            }
            if (unity != null) {
                getRepository().delete(unity);
                return unity;
            }
        }
        return null;
    }

    /**
     * 根据标识删除从属单体前调用，由子类覆写<br/>
     * 子类不覆写或调用父类的本方法，将无法删除单体
     *
     * @param owner 所属者
     * @param id    要删除的单体的标识
     * @return 要删除的单体，可返回null，返回非null值有助于提高性能
     */
    protected T beforeDelete(O owner, K id) {
        throw new UnsupportedOperationException();
    }

}
