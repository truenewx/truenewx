package org.truenewx.tnxjee.test.service.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.repo.support.RepoFactory;

/**
 * 单元测试数据提供者工厂
 */
@Component
public class TestDataProviderFactory implements DataProviderFactory, ContextInitializedBean {
    @Autowired
    private RepoFactory repositoryFacotory;
    private Map<Class<?>, DataProvider<?>> providers = new HashMap<>();

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        @SuppressWarnings("rawtypes")
        Map<String, DataProvider> beans = context.getBeansOfType(DataProvider.class);
        beans.values().forEach(provider -> {
            Class<?> entityClass = ClassUtil.getActualGenericType(provider.getClass(), DataProvider.class, 0);
            this.providers.put(entityClass, provider);
        });
    }

    @Override
    public void init(Class<?>... entityClasses) {
        if (ArrayUtils.isEmpty(entityClasses)) {
            this.providers.values().forEach(provider -> {
                provider.getDataList(this);
            });
        } else {
            for (Class<?> entityClass : entityClasses) {
                DataProvider<?> provider = this.providers.get(entityClass);
                if (provider != null) {
                    provider.getDataList(this);
                }
            }
        }
    }

    @Override
    public <T extends Entity> List<T> getDataList(Class<T> entityClass) {
        @SuppressWarnings("unchecked")
        DataProvider<T> provider = (DataProvider<T>) this.providers.get(entityClass);
        if (provider != null) {
            return provider.getDataList(this);
        } else {
            CrudRepository<T, ?> repo = this.repositoryFacotory.getRepository(entityClass);
            if (repo != null) {
                return CollectionUtil.toList(repo.findAll());
            }
        }
        return null;
    }

    @Override
    public void clear(Class<?>... entityClasses) {
        if (ArrayUtils.isEmpty(entityClasses)) {
            this.providers.values().forEach(provider -> {
                provider.clear();
            });
        } else {
            for (Class<?> entityClass : entityClasses) {
                DataProvider<?> provider = this.providers.get(entityClass);
                if (provider != null) {
                    provider.clear();
                }
            }
        }
    }
}
