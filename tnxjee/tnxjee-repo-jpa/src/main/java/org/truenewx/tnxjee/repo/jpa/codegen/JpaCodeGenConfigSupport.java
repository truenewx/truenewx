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
        return new JpaRepoGeneratorImpl(getModelBasePackage(), getRepoBasePackage());
    }

}
