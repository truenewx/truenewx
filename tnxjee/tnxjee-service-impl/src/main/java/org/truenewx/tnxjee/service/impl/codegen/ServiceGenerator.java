package org.truenewx.tnxjee.service.impl.codegen;

import org.truenewx.tnxjee.model.entity.Entity;

/**
 * 服务类生成器
 */
public interface ServiceGenerator {

    void generate(String... modules) throws Exception;

    void generate(Class<? extends Entity> entityClass) throws Exception;

}
