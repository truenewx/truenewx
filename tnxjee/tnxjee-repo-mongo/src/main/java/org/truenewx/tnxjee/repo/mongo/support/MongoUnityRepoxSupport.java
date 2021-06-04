package org.truenewx.tnxjee.repo.mongo.support;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.repo.UnityRepox;

/**
 * MongoDB单体数据访问仓库扩展支持
 *
 * @author jianglei
 */
public abstract class MongoUnityRepoxSupport<T extends Unity<K>, K extends Serializable> extends MongoRepoxSupport<T>
        implements UnityRepox<T, K> {

    protected final T find(K id) {
        CrudRepository<T, K> repository = getRepository();
        return repository.findById(id).orElse(null);
    }

    @Override
    public <N extends Number> T increaseNumber(K id, String propertyName, N step, N limit) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
