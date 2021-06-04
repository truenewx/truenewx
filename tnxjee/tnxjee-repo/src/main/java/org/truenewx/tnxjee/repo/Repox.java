package org.truenewx.tnxjee.repo;

import org.springframework.data.repository.Repository;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.entity.Entity;

/**
 * 数据访问仓库扩展<br/>
 * 用于定义在spring-data的{@link Repository}标准规范方法之外的扩展的数据访问方法<br/>
 * 一个实体类型必须要有对应的{@link Repository}，但可以没有对应的{@link Repox}
 *
 * @param <T> 实体类型
 * @author jianglei
 */
public interface Repox<T extends Entity> {

    default String getEntityName() {
        return ClassUtil.getActualGenericType(getClass(), Repox.class, 0).getName();
    }

}
