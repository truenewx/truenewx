package org.truenewx.tnxjee.repo.jpa.codegen;

import org.truenewx.tnxjee.model.entity.Entity;

/**
 * JPA实体映射文件生成器
 *
 * @author jianglei
 */
public interface JpaEntityMappingGenerator {

    void generate(String... modules) throws Exception;

    void generate(Class<? extends Entity> entityClass, String tableName) throws Exception;

}
