package org.truenewx.tnxjee.repo.jpa.validation.config;

import java.beans.PropertyDescriptor;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Iterator;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.MappingException;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spec.PermanentableDate;
import org.truenewx.tnxjee.model.Model;
import org.truenewx.tnxjee.model.ValueModel;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.validation.config.ValidationConfiguration;
import org.truenewx.tnxjee.model.validation.rule.DecimalRule;
import org.truenewx.tnxjee.model.validation.rule.LengthRule;
import org.truenewx.tnxjee.model.validation.rule.MarkRule;
import org.truenewx.tnxjee.repo.jpa.support.JpaAccessTemplate;
import org.truenewx.tnxjee.repo.support.DataAccessTemplate;
import org.truenewx.tnxjee.repo.support.DataAccessTemplateFactory;
import org.truenewx.tnxjee.repo.validation.config.DefaultValidationConfigurationFactory;

/**
 * JPA的字段校验配置工厂
 *
 * @author jianglei
 */
@Component
public class JpaValidationConfigurationFactory extends DefaultValidationConfigurationFactory {

    private ValidationEntityNameStrategy entityNameStrategy = ValidationEntityNameStrategy.DEFAULT;
    @Autowired
    private DataAccessTemplateFactory dataAccessTemplateFactory;

    @Autowired(required = false)
    public void setEntityNameStrategy(ValidationEntityNameStrategy entityNameStrategy) {
        if (entityNameStrategy != null) {
            this.entityNameStrategy = entityNameStrategy;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ValidationConfiguration buildConfiguration(Class<? extends Model> modelClass) {
        ValidationConfiguration configuration = super.buildConfiguration(modelClass);
        if (Entity.class.isAssignableFrom(modelClass)) {
            addEntityClassRulesFromPersistentConfig(configuration, (Class<? extends Entity>) modelClass);
        }
        return configuration;
    }

    /**
     * 从指定实体类对应的持久化配置中添加校验规则到指定校验配置中
     *
     * @param configuration 校验配置
     * @param entityClass   实体类
     */
    @SuppressWarnings("unchecked")
    private void addEntityClassRulesFromPersistentConfig(ValidationConfiguration configuration,
            Class<? extends Entity> entityClass) {
        DataAccessTemplate accessTemplate = this.dataAccessTemplateFactory.getDataAccessTemplate(entityClass);
        if (accessTemplate instanceof JpaAccessTemplate) {
            JpaAccessTemplate jat = (JpaAccessTemplate) accessTemplate;
            String entityName = this.entityNameStrategy.getEntityName(entityClass);
            PersistentClass persistentClass = jat.getPersistentClass(entityName);
            if (persistentClass != null) {
                Iterator<Property> properties = persistentClass.getPropertyIterator();
                while (properties.hasNext()) {
                    Property property = properties.next();
                    PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(entityClass,
                            property.getName());
                    if (propertyDescriptor != null) {
                        addRuleByPersistentProperty(configuration, property, propertyDescriptor, null);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addRuleByPersistentProperty(ValidationConfiguration configuration, Property property,
            PropertyDescriptor propertyDescriptor, String propertyNamePrefix) {
        if (StringUtils.isBlank(propertyNamePrefix)) { // 前缀默认为空
            propertyNamePrefix = Strings.EMPTY;
        }
        String propertyName = propertyDescriptor.getName();
        Class<?> propertyClass = propertyDescriptor.getPropertyType();
        if (supports(propertyClass)) {
            Iterator<Column> columns = property.getColumnIterator();
            // 只支持对应且仅对应一个物理字段的
            if (!columns.hasNext()) {
                return;
            }
            Column column = columns.next();
            if (columns.hasNext()) {
                return;
            }
            propertyName = propertyNamePrefix + propertyName;
            if (CharSequence.class.isAssignableFrom(propertyClass)) { // 字符串型
                int maxLength = column.getLength();
                if (maxLength > 0) { // 长度大于0才有效
                    LengthRule rule = configuration.getRule(propertyName, LengthRule.class, LengthRule::new);
                    if (rule.getMax() > maxLength) {
                        rule.setMax(maxLength);
                    }
                }
            } else if (propertyClass.isEnum()) { // 枚举型
                if (!column.isNullable()) { // 不允许为null的枚举型，添加不允许为空白的约束
                    configuration.addRule(propertyName, new MarkRule(NotBlank.class));
                }
            } else if (Date.class.isAssignableFrom(propertyClass)
                    || Temporal.class.isAssignableFrom(propertyClass)
                    || PermanentableDate.class.isAssignableFrom(propertyClass)) { // 日期型
                if (!column.isNullable()) { // 不允许为null的日期型，添加不允许为空白的约束
                    configuration.addRule(propertyName, new MarkRule(NotBlank.class));
                }
            } else if (propertyClass.isPrimitive() || Number.class.isAssignableFrom(propertyClass)) { // 数值型
                if (!column.isNullable()) { // 不允许为null的数值型，添加不允许为空白的约束
                    configuration.addRule(propertyName, new MarkRule(NotBlank.class));
                }
                int precision = column.getPrecision();
                int scale = column.getScale();
                if (propertyClass == long.class || propertyClass == Long.class) {
                    if (precision > 20) {
                        precision = 20;
                    }
                    scale = 0;
                } else if (propertyClass == int.class || propertyClass == Integer.class) {
                    if (precision > 11) {
                        precision = 11;
                    }
                    scale = 0;
                } else if (propertyClass == short.class || propertyClass == Short.class) {
                    if (precision > 5) {
                        precision = 5;
                    }
                    scale = 0;
                } else if (propertyClass == byte.class || propertyClass == Byte.class) {
                    if (precision > 3) {
                        precision = 3;
                    }
                    scale = 0;
                }
                if (scale >= 0 && precision > scale) { // 精度大于等于0且长度大于精度才有效，不支持负精度
                    DecimalRule rule = new DecimalRule();
                    rule.setPrecision(precision);
                    rule.setScale(scale);
                    configuration.addRule(propertyName, rule);
                }
            }
        } else if (ValueModel.class.isAssignableFrom(propertyClass)) {
            PersistentClass persistentClass = property.getPersistentClass();
            propertyNamePrefix += propertyName + Strings.DOT;
            PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(propertyClass);
            for (PropertyDescriptor pd : pds) {
                // 必须同时有读方法和写方法才视为有效属性
                if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
                    String propertyPath = propertyNamePrefix + pd.getName();
                    try {
                        Property referencedProperty = persistentClass.getReferencedProperty(propertyPath);
                        addRuleByPersistentProperty(configuration, referencedProperty, pd, propertyNamePrefix);
                    } catch (MappingException ignored) {
                    }
                }
            }
        }
    }

    private boolean supports(Class<?> propertyClass) {
        // 支持字符串
        if (CharSequence.class.isAssignableFrom(propertyClass)) {
            return true;
        }
        // 支持除布尔类型外的原生类型，也就是原生数值
        if (propertyClass.isPrimitive() && propertyClass != boolean.class) {
            return true;
        }
        // 支持数值类型
        if (Number.class.isAssignableFrom(propertyClass)) {
            return true;
        }
        // 支持日期型
        if (Date.class.isAssignableFrom(propertyClass) || Temporal.class.isAssignableFrom(propertyClass)
                || PermanentableDate.class.isAssignableFrom(propertyClass)) {
            return true;
        }
        // 支持枚举
        return propertyClass.isEnum();
    }

}
