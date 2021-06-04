package org.truenewx.tnxjee.model.validation.config;

import org.truenewx.tnxjee.model.Model;

/**
 * 校验配置工厂
 *
 * @author jianglei
 */
public interface ValidationConfigurationFactory {
    /**
     * 获取指定模型类的校验配置
     *
     * @param modelClass 模型类
     * @return 指定模型类的校验配置
     */
    ValidationConfiguration getConfiguration(Class<? extends Model> modelClass);
}
