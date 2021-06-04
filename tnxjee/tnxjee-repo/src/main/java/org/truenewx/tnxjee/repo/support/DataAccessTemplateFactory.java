package org.truenewx.tnxjee.repo.support;

/**
 * 数据访问模板工厂
 *
 * @author jianglei
 */
public interface DataAccessTemplateFactory {

    DataAccessTemplate getDataAccessTemplate(Class<?> entityClass);

}
