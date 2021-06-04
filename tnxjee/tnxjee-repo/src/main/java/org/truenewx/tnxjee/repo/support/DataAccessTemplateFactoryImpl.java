package org.truenewx.tnxjee.repo.support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;

/**
 * 数据模板工厂实现
 *
 * @author jianglei
 */
@Component
public class DataAccessTemplateFactoryImpl implements DataAccessTemplateFactory, ContextInitializedBean {

    private Map<Class<?>, DataAccessTemplate> templateMappings = new HashMap<>();

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        context.getBeansOfType(DataAccessTemplate.class).values().forEach(template -> {
            template.getEntityClasses().forEach(entityClass -> {
                this.templateMappings.put(entityClass, template);
            });
        });
    }

    @Override
    public DataAccessTemplate getDataAccessTemplate(Class<?> entityClass) {
        return this.templateMappings.get(entityClass);
    }

}
