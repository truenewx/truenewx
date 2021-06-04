package org.truenewx.tnxjee.repo.jpa.codegen;

import org.truenewx.tnxjee.model.entity.Entity;

/**
 * JPA Repo类生成器
 *
 * @author jianglei
 */
public interface JpaRepoGenerator {

    void generate(String... modules) throws Exception;

    void generate(Class<? extends Entity> entityClass, boolean withImpl) throws Exception;

}
