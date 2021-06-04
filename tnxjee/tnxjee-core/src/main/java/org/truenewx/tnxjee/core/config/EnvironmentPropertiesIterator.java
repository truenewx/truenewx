package org.truenewx.tnxjee.core.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.*;

import java.util.function.BiConsumer;

/**
 * 环境属性迭代器
 */
public class EnvironmentPropertiesIterator implements EnvironmentAware {

    private BiConsumer<String, Object> consumer;

    public EnvironmentPropertiesIterator(BiConsumer<String, Object> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void setEnvironment(Environment environment) {
        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
        MutablePropertySources propertySources = env.getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            if (propertySource instanceof EnumerablePropertySource) {
                EnumerablePropertySource<?> source = (EnumerablePropertySource<?>) propertySource;
                String[] propertyNames = source.getPropertyNames();
                for (String propertyName : propertyNames) {
                    Object propertyValue = source.getProperty(propertyName);
                    this.consumer.accept(propertyName, propertyValue);
                }
            }
        }
    }

}
