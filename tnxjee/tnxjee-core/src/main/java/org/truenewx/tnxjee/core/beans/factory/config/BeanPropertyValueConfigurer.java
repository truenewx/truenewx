package org.truenewx.tnxjee.core.beans.factory.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.BeanUtil;

/**
 * Bean属性值配置器
 *
 * @author jianglei
 */
public class BeanPropertyValueConfigurer implements InitializingBean, ContextInitializedBean {
    /**
     * Bean对象
     */
    private Object bean;
    /**
     * Bean名称
     */
    private String beanName;
    /**
     * Bean类型
     */
    private Class<?> beanClass;
    /**
     * 属性名
     */
    private String propertyName;
    /**
     * 属性值
     */
    private Object propertyValue;

    /**
     * @param bean Bean对象
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }

    /**
     * @param beanName Bean名称
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * @param beanClass Bean类型
     */
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * @param name 属性名
     */
    public void setPropertyName(String name) {
        this.propertyName = name;
    }

    /**
     * @param value 属性值
     */
    public void setPropertyValue(Object value) {
        this.propertyValue = value;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.bean != null) {
            BeanUtil.setPropertyValue(this.bean, this.propertyName, this.propertyValue);
        }
    }

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        if (this.bean == null) {
            Assert.isTrue(this.beanName != null || this.beanClass != null, "beanName or beanClass must be not null"); // Bean名称或类型至少一个不为null
            if (this.beanName != null && this.beanClass != null) {
                this.bean = context.getBean(this.beanName, this.beanClass);
            } else if (this.beanName != null) {
                this.bean = context.getBean(this.beanName);
            } else if (this.beanClass != null) {
                this.bean = context.getBean(this.beanClass);
            }
            afterPropertiesSet();
        }
    }

}
