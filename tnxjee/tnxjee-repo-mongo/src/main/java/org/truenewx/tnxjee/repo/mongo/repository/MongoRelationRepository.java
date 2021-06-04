package org.truenewx.tnxjee.repo.mongo.repository;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.truenewx.tnxjee.model.entity.relation.Relation;
import org.truenewx.tnxjee.model.entity.relation.RelationKey;
import org.truenewx.tnxjee.repo.RelationRepox;

/**
 * 为JPA关系访问库提供的便捷接口，非必须，业务Repo接口可同时继承{@link MongoRepository}和{@link RelationRepox}达到相同的效果
 *
 * @author jianglei
 */
@NoRepositoryBean
public interface MongoRelationRepository<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
        extends MongoRepository<T, RelationKey<L, R>>, RelationRepox<T, L, R> {

    @Override
    default Optional<T> findById(RelationKey<L, R> id) {
        return Optional.ofNullable(find(id.getLeft(), id.getRight()));
    }

}
