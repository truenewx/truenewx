package org.truenewx.tnxjee.repo.jpa.support;

import java.io.Serializable;
import java.util.List;

import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.entity.HistoryEntity;
import org.truenewx.tnxjee.model.entity.unity.OwnedUnity;

/**
 * 可历史化单体JPA数据访问仓库扩展支持
 *
 * @param <T> 可历史化单体类型
 * @param <K> 标识类型
 * @param <O> 所属者类型
 */
public abstract class JpaHistorizableOwnedUnityRepoxSupport<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends JpaOwnedUnityRepoSupport<T, K, O> {

    protected String[] getEntityNames() {
        return new String[]{ getEntityName(), getHistoryEntityName() };
    }

    protected abstract Class<? extends HistoryEntity<T>> getHistoryEntityClass();

    protected String getHistoryEntityName() {
        return getHistoryEntityClass().getName();
    }

    protected Class<? extends Entity> getEntityClass(boolean historized) {
        return historized ? getHistoryEntityClass() : getEntityClass();
    }

    protected String getEntityName(boolean historized) {
        return getEntityClass(historized).getName();
    }

    @SuppressWarnings("unchecked")
    protected T toPresent(Object obj) {
        if (obj != null) {
            if (getHistoryEntityClass().isInstance(obj)) {
                return ((HistoryEntity<T>) obj).toPresent();
            }
            // 如果实例既不是历史实体也不是当前实体，同样强制造型，以抛出错误，提醒代码有误
            return (T) obj;
        }
        return null;
    }

    protected List<T> toPresents(List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            T entity = toPresent(obj);
            if (entity != obj) {
                list.set(i, entity);
            }
        }
        return list;
    }

}
