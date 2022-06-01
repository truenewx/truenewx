package org.truenewx.tnxjee.repo.jpa.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.model.entity.unity.OwnedUnity;
import org.truenewx.tnxjee.repo.OwnedUnityRepox;

/**
 * 从属单体的数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class JpaOwnedUnityRepoxSupport<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends JpaUnityRepoxSupport<T, K> implements OwnedUnityRepox<T, K, O> {

    /**
     * 获取所属者属性名<br>
     * 默认返回null，此时通过标识获取单体后判断所属者是否匹配，可由子类覆写返回非null的值，从而通过所属字段限制单体查询<br>
     * 建议：当所属者为引用对象下的属性时 ，子类覆写提供非null的返回值，否则不覆写
     *
     * @return 所属者属性
     */
    protected String getOwnerProperty() {
        return null;
    }

    @Override
    public long countByOwner(O owner) {
        String ownerProperty = getOwnerProperty();
        if (ownerProperty == null) {
            throw new UnsupportedOperationException();
        }
        StringBuilder ql = new StringBuilder("select count(*) from ").append(getEntityName())
                .append(" e where e.").append(ownerProperty).append("=:owner");
        return getAccessTemplate().count(ql.toString(), "owner", owner);
    }

    @Override
    public T findByOwnerAndId(O owner, K id) {
        if (id == null) {
            return null;
        }
        String ownerProperty = getOwnerProperty();
        if (ownerProperty == null) {
            T entity = find(id);
            if (entity != null && owner.equals(entity.getOwner())) {
                return entity;
            }
            return null;
        }
        StringBuilder ql = new StringBuilder("from ").append(getEntityName()).append(" e where e.")
                .append(ownerProperty).append("=:owner and e.id=:id");
        Map<String, Object> params = new HashMap<>();
        params.put("owner", owner);
        params.put("id", id);
        return getAccessTemplate().first(ql.toString(), params);
    }

    @Override
    public <N extends Number> T increaseNumber(O owner, K id, String propertyName, N step, N limit) {
        double stepValue = step.doubleValue();
        if (stepValue != 0) { // 增量不为0时才处理
            Map<String, Object> params = new HashMap<>();
            StringBuilder ql = buildIncreaseQl(id, propertyName, step, params);

            String ownerProperty = getOwnerProperty();
            if (owner != null && ownerProperty != null) {
                ql.append(" and ").append(ownerProperty).append("=:owner");
                params.put("owner", owner);
            }

            if (doIncreaseNumber(ql, params, propertyName, stepValue > 0, limit)) {
                // 更新字段后需刷新实体
                T unity = find(id);
                try {
                    refresh(unity);
                } catch (Exception e) { // 忽略刷新失败
                    LogUtil.error(getClass(), e);
                }
                return unity;
            }
        }
        return null;
    }

}
