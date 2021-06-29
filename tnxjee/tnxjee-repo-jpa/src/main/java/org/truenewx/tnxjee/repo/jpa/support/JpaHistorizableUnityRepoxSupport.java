package org.truenewx.tnxjee.repo.jpa.support;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.HistoryEntity;
import org.truenewx.tnxjee.model.entity.unity.Unity;

/**
 * 可历史化单体JPA数据访问仓库扩展支持
 *
 * @param <T> 可历史化单体类型
 * @param <K> 标识类型
 */
public abstract class JpaHistorizableUnityRepoxSupport<T extends Unity<K>, K extends Serializable>
        extends JpaUnityRepoxSupport<T, K> {

    protected String getEntityName(boolean historized) {
        if (historized) {
            return getHistoryEntityName();
        } else {
            return getEntityName();
        }
    }

    protected String[] getEntityNames(boolean historyFirst) {
        if (historyFirst) {
            return new String[]{ getHistoryEntityName(), getEntityName() };
        } else {
            return new String[]{ getEntityName(), getHistoryEntityName() };
        }
    }

    protected abstract Class<? extends HistoryEntity<T>> getHistoryEntityClass();

    protected String getHistoryEntityName() {
        return getHistoryEntityClass().getName();
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

}
