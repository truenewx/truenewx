package org.truenewx.tnxjee.repo.jpa.validation.config;

/**
 * 校验实体名称策略<br>
 * 可通过子类实现更复杂的实体名称获取策略
 *
 * @author jianglei
 */
public class ValidationEntityNameStrategy {
    /**
     * 默认实例
     */
    public static final ValidationEntityNameStrategy DEFAULT = new ValidationEntityNameStrategy();

    /**
     * 仅子类可创建实例
     */
    protected ValidationEntityNameStrategy() {
    }

    /**
     * 获取指定实体类型的实体名称
     *
     * @param entityClass 实体类型
     * @return 实体名称
     */
    public String getEntityName(Class<?> entityClass) {
        return entityClass.getName();
    }

}
