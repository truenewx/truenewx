package org.truenewx.tnxjee.repo.mongo.repository;

import java.io.Serializable;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.repo.UnityRepox;

/**
 * 为Mongo单体访问库提供的便捷接口，非必须，业务Repo接口可同时继承{@link MongoRepository}和{@link UnityRepox}达到相同的效果
 *
 * @author jianglei
 */
@NoRepositoryBean
public interface MongoUnityRepository<T extends Unity<K>, K extends Serializable>
        extends MongoRepository<T, K>, UnityRepox<T, K> {

}
