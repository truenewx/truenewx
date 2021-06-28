package org.truenewx.tnxjee.repo.jpa;

import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.repo.Repox;

/**
 * JPA的数据访问仓库扩展
 *
 * @author jianglei
 */
public interface JpaRepox<T extends Entity> extends Repox<T> {

    void flush();

    void refresh(T entity);

}
