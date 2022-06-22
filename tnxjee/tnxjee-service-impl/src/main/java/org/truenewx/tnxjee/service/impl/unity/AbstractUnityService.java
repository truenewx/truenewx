package org.truenewx.tnxjee.service.impl.unity;

import java.io.Serializable;

import org.springframework.util.Assert;
import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.service.impl.AbstractService;
import org.truenewx.tnxjee.service.unity.CommandUnityService;
import org.truenewx.tnxjee.service.unity.SimpleUnityService;

/**
 * 抽象的单体服务
 *
 * @param <T> 单体类型
 * @param <K> 单体标识类型
 * @author jianglei
 */
public abstract class AbstractUnityService<T extends Unity<K>, K extends Serializable>
        extends AbstractService<T> implements SimpleUnityService<T, K>, CommandUnityService<T, K> {

    protected static final String MESSAGE_ID_NOT_EQUAL = "id must equal unity's id";

    @Override
    public T find(K id) {
        return id == null ? null : getRepository().findById(id).orElse(null);
    }

    @Override
    public T load(K id) {
        T unity = find(id);
        assertNotNull(unity);
        return unity;
    }

    @Override
    public T add(T unity) {
        T newUnity = beforeSave(null, unity);
        Assert.isTrue(newUnity != unity, "the returned unity must not be the input unity");
        doAdd(newUnity);
        return newUnity;
    }

    private void doAdd(T unity) {
        if (unity != null) {
            unity = save(unity);
            afterSave(unity);
        }
    }

    protected T save(T unity) {
        // 没有id的单体保存后需取保存结果才能获得id
        unity = getRepository().save(unity);
        return unity;
    }

    @Override
    public T update(K id, T unity) {
        if (id == null) {
            return null;
        }
        T newUnity = beforeSave(id, unity);
        doUpdate(id, newUnity);
        return newUnity;
    }

    private void doUpdate(K id, T unity) {
        if (unity != null) {
            Assert.isTrue(id.equals(unity.getId()), MESSAGE_ID_NOT_EQUAL);
            unity = save(unity);
            afterSave(unity);
        }
    }

    /**
     * 在保存添加/修改单体前调用，负责验证改动的单体数据，并写入返回的结果单体中<br>
     * <strong>注意：</strong>子类覆写时应确保结果不为null（否则将不保存）
     *
     * @param id    要修改的单体标识，为null时表示是添加动作
     * @param unity 存放添加/修改数据的单体对象
     * @return 已写入数据，即将保存的单体
     */
    protected T beforeSave(K id, T unity) {
        throw new UnsupportedOperationException();
    }

    /**
     * 单体保存之后调用，负责后续处理
     *
     * @param unity 被保存的单体
     */
    protected void afterSave(T unity) {
    }

    @Override
    public T add(CommandModel<T> commandModel) {
        T unity = beforeSave(null, commandModel);
        doAdd(unity);
        return unity;
    }

    @Override
    public T update(K id, CommandModel<T> commandModel) {
        if (id == null) {
            return null;
        }
        T unity = beforeSave(id, commandModel);
        doUpdate(id, unity);
        return unity;
    }

    /**
     * 在保存添加/修改单体前调用，负责验证改动的模型数据，并写入返回的结果单体中<br>
     * <strong>注意：</strong>子类覆写时应确保结果不为null（否则将不保存）
     *
     * @param id           要修改的单体标识，为null时表示是添加动作
     * @param commandModel 存放添加/修改数据的命令模型
     * @return 已写入数据，即将保存的单体
     */
    protected T beforeSave(K id, CommandModel<T> commandModel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T delete(K id) {
        if (id != null) {
            T unity = beforeDelete(id);
            if (unity == null) {
                unity = find(id);
            }
            delete(unity);
            return unity;
        }
        return null;
    }

    /**
     * 根据标识删除单体前调用，由子类覆写<br>
     * 不覆写或子类调用父类的本方法，将无法删除单体
     *
     * @param id 要删除的单体的标识
     * @return 要删除的单体，可返回null，返回非null值有助于提高性能
     */
    protected T beforeDelete(K id) {
        throw new UnsupportedOperationException();
    }

    protected void delete(T unity) {
        if (unity != null) {
            getRepository().delete(unity);
        }
    }

}
