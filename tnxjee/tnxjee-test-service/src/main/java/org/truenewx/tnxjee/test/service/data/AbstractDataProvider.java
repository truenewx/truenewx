package org.truenewx.tnxjee.test.service.data;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.repo.support.RepoFactory;

/**
 * 抽象的数据提供者
 *
 * @author jianglei
 */
public abstract class AbstractDataProvider<T extends Entity> implements DataProvider<T> {
    @Autowired
    private RepoFactory repoFactory;

    private <R extends CrudRepository<T, K>, K> R getRepository() {
        return this.repoFactory.getRepository(getEntityClass());
    }

    private Class<T> getEntityClass() {
        return ClassUtil.getActualGenericType(getClass(), 0);
    }

    @Override
    public List<T> getDataList(DataPool pool) {
        if (getRepository().count() == 0) {
            init(pool);
        }
        return CollectionUtil.toList(getRepository().findAll());
    }

    protected final void save(T entity) {
        getRepository().save(entity);
    }

    protected abstract void init(DataPool pool);

    @Override
    public void clear() {
        getRepository().deleteAll();
    }

}
