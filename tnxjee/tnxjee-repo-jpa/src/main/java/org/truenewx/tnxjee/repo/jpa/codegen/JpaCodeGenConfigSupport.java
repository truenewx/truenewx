package org.truenewx.tnxjee.repo.jpa.codegen;

import org.springframework.context.annotation.Bean;

/**
 * JPA代码生成配置支持
 *
 * @author jianglei
 */
public abstract class JpaCodeGenConfigSupport {

    /**
     * 获取模型基础包名称，用于定位扫描的模型类存放的位置
     *
     * @return 模型基础包名称
     */
    protected abstract String getModelBasePackage();

    /**
     * 获取Repo基础包名称，用于定位生成的Repo类存放的位置
     *
     * @return Repo基础包名称
     */
    protected String getRepoBasePackage() {
        return getClass().getPackageName();
    }

    /**
     * 获取不生成Repo的实体类型清单
     *
     * @return 不生成Repo的实体类型清单
     */
    protected Class<?>[] getIgnoredEntityClassesForRepo() {
        return null;
    }

    @Bean
    public JpaEnumConverterGenerator enumConverterGenerator() {
        return new JpaEnumConverterGeneratorImpl(getModelBasePackage(), getRepoBasePackage());
    }

    @Bean
    public JpaEntityMappingGenerator entityMappingGenerator() {
        return new JpaEntityMappingGeneratorImpl(getModelBasePackage());
    }

    @Bean
    public JpaRepoGenerator repoGenerator() {
        return new JpaRepoGeneratorImpl(getModelBasePackage(), getRepoBasePackage(), getIgnoredEntityClassesForRepo());
    }

}
