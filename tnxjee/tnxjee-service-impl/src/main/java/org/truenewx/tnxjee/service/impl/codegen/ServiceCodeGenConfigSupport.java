package org.truenewx.tnxjee.service.impl.codegen;

import org.springframework.context.annotation.Bean;

/**
 * Service代码生成配置支持
 *
 * @author jianglei
 */
public abstract class ServiceCodeGenConfigSupport {

    /**
     * 获取模型基础包名称，用于定位扫描的模型类存放的位置
     *
     * @return 模型基础包名称
     */
    protected abstract String getModelBasePackage();

    /**
     * 获取Service基础包名称，用于定位生成的Service类存放的位置
     *
     * @return Service基础包名称
     */
    protected String getServiceBasePackage() {
        return getClass().getPackageName();
    }

    @Bean
    public ServiceGenerator serviceGenerator() {
        return new ServiceGeneratorImpl(getModelBasePackage(), getServiceBasePackage());
    }

}
