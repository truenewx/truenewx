package org.truenewx.tnxjee.repo.support;

import org.springframework.data.repository.CrudRepository;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.repo.Repox;

/**
 * 数据访问仓库工厂
 *
 * @author jianglei
 */
public interface RepoFactory {

    <R extends CrudRepository<T, K>, T extends Entity, K> R getRepository(Class<T> entityClass);

    <R extends Repox<T>, T extends Entity> R getRepo(Class<T> entityClass);

}
