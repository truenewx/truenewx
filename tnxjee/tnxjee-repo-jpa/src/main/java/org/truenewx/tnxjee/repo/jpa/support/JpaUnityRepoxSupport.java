package org.truenewx.tnxjee.repo.jpa.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.repo.UnityRepox;

/**
 * 单体JPA数据访问仓库扩展支持
 *
 * @author jianglei
 */
public abstract class JpaUnityRepoxSupport<T extends Unity<K>, K extends Serializable> extends JpaRepoxSupport<T>
        implements UnityRepox<T, K> {

    protected final T find(K id) {
        return getRepository().findById(id).orElse(null);
    }

    @Override
    public <N extends Number> T increaseNumber(K id, String propertyName, N step, N limit) {
        double stepValue = step.doubleValue();
        if (stepValue != 0) { // 增量不为0时才处理
            Map<String, Object> params = new HashMap<>();
            StringBuffer ql = buildIncreaseQl(id, propertyName, step, params);

            if (doIncreaseNumber(ql, params, propertyName, stepValue > 0, limit)) {
                // 正确更新字段后需刷新实体
                T entity = find(id);
                if (entity != null) {
                    try {
                        refresh(entity);
                    } catch (Exception e) { // 忽略刷新失败
                        LogUtil.error(getClass(), e);
                    }
                }
                return entity;
            }
        }
        return null;
    }

    protected <N extends Number> StringBuffer buildIncreaseQl(K id, String propertyName, N step,
            Map<String, Object> params) {
        String entityName = getEntityName();
        StringBuffer ql = new StringBuffer("update ").append(entityName).append(" set ").append(propertyName)
                .append(Strings.EQUAL).append(propertyName).append("+:step where id=:id");
        params.put("id", id);
        params.put("step", step);
        return ql;
    }

}
