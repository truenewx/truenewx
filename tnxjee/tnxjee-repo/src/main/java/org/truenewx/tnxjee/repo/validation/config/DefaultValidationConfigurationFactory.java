package org.truenewx.tnxjee.repo.validation.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import javax.validation.Constraint;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxjee.model.Model;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.validation.annotation.InheritConstraint;
import org.truenewx.tnxjee.model.validation.config.ValidationConfiguration;
import org.truenewx.tnxjee.model.validation.config.ValidationConfigurationFactory;
import org.truenewx.tnxjee.model.validation.rule.MarkRule;
import org.truenewx.tnxjee.model.validation.rule.ValidationRule;
import org.truenewx.tnxjee.repo.validation.rule.builder.ValidationRuleBuilder;

/**
 * 默认的字段校验配置工厂
 *
 * @author jianglei
 */
public class DefaultValidationConfigurationFactory implements ValidationConfigurationFactory, ContextInitializedBean {

    private Map<Class<? extends Model>, ValidationConfiguration> configurations = new HashMap<>();
    private Map<Class<Annotation>, ValidationRuleBuilder<?>> ruleBuilders = new HashMap<>();

    @SuppressWarnings("unchecked")
    public void setValidationRuleBuilders(Collection<ValidationRuleBuilder<?>> builders) {
        for (ValidationRuleBuilder<?> builder : builders) {
            for (Class<?> constraintType : builder.getConstraintTypes()) {
                Assert.isTrue(isConstraintAnnotation(constraintType), "constraintType must be constraint annotation");
                // setter方法设置的构建器优先，会覆盖掉已有的构建器
                this.ruleBuilders.put((Class<Annotation>) constraintType, builder);
            }
        }
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void afterInitialized(ApplicationContext context) throws Exception {
        Map<String, ValidationRuleBuilder> beans = context.getBeansOfType(ValidationRuleBuilder.class);
        for (ValidationRuleBuilder<?> builder : beans.values()) {
            for (Class<?> constraintType : builder.getConstraintTypes()) {
                Assert.isTrue(isConstraintAnnotation(constraintType), "constraintType must be constraint annotation");
                // 不覆盖通过setter方法设置的构建器
                if (!this.ruleBuilders.containsKey(constraintType)) {
                    this.ruleBuilders.put((Class<Annotation>) constraintType, builder);
                }
            }
        }
    }

    private boolean isConstraintAnnotation(Class<?> annoClass) {
        return annoClass.getAnnotation(Constraint.class) != null;
    }

    @Override
    public ValidationConfiguration getConfiguration(Class<? extends Model> modelClass) {
        ValidationConfiguration configuration = this.configurations.get(modelClass);
        if (configuration == null) {
            configuration = buildConfiguration(modelClass);
            this.configurations.put(modelClass, configuration);
        }
        return configuration;
    }

    @SuppressWarnings("unchecked")
    protected ValidationConfiguration buildConfiguration(Class<? extends Model> modelClass) {
        ValidationConfiguration configuration = new ValidationConfiguration(modelClass);
        if (CommandModel.class.isAssignableFrom(modelClass)) {
            addEntityClassRulesFromCommandModelClass(configuration, (Class<? extends CommandModel<?>>) modelClass);
        }
        addRulesByAnnotation(configuration, modelClass);
        return configuration;
    }

    /**
     * 从指定命令模型类对应的实体类中添加校验规则到指定校验配置中
     *
     * @param configuration     校验配置
     * @param commandModelClass 命令模型类
     */
    private void addEntityClassRulesFromCommandModelClass(ValidationConfiguration configuration,
            Class<? extends CommandModel<?>> commandModelClass) {
        Class<? extends Entity> entityClass = ClassUtil.getActualGenericType(commandModelClass, CommandModel.class, 0);
        List<Field> fields = ClassUtil.getSimplePropertyField(commandModelClass);
        for (Field field : fields) {
            // 加入对应实体的校验规则
            // 只加入传输模型中存在的简单属性的校验规则
            Class<? extends Entity> entityType = entityClass;
            String propertyName = field.getName();
            InheritConstraint ic = field.getAnnotation(InheritConstraint.class);
            if (ic != null) {
                if (StringUtils.isNotBlank(ic.value())) {
                    propertyName = ic.value();
                }
                if (ic.type() != Entity.class) {
                    entityType = ic.type();
                }
            }
            if (entityType != null) {
                ValidationConfiguration entityConfig = getConfiguration(entityType);
                if (entityConfig != null) {
                    Set<ValidationRule> rules = entityConfig.getRules(propertyName);
                    if (rules != null && rules.size() > 0) {
                        configuration.getRules(field.getName()).addAll(rules);
                    }
                }
            }
            addRulesByPropertyAnnotations(configuration, field);
        }
    }

    /**
     * 从指定类的校验约束注解中添加校验规则到指定校验配置中
     *
     * @param configuration 校验配置
     * @param clazz         类
     */
    private void addRulesByAnnotation(ValidationConfiguration configuration, Class<?> clazz) {
        List<Field> fields = ClassUtil.getSimplePropertyField(clazz);
        for (Field field : fields) {
            addRulesByPropertyAnnotations(configuration, field);
        }
    }

    private void addRulesByPropertyAnnotations(ValidationConfiguration configuration, Field field) {
        String propertyName = field.getName();
        // 先在属性字段上找约束注解生成规则
        for (Annotation annotation : field.getAnnotations()) {
            addRuleByPropertyAnnotation(configuration, propertyName, annotation);
        }
        // 再尝试在属性的setter方法上找约束注解生成规则，这意味着setter方法上的约束注解优先级更高
        Method method = ClassUtil.findPropertyMethod(field.getDeclaringClass(), propertyName, false);
        if (method != null) {
            for (Annotation annotation : method.getAnnotations()) {
                addRuleByPropertyAnnotation(configuration, propertyName, annotation);
            }
        }
        // 确保原生类型的字段具有非空规则
        if (field.getType().isPrimitive()) {
            MarkRule markRule = configuration.getRule(propertyName, MarkRule.class);
            if (markRule == null) {
                markRule = new MarkRule(NotNull.class);
                configuration.addRule(propertyName, markRule);
            } else {
                markRule.getAnnotationTypes().add(NotNull.class);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addRuleByPropertyAnnotation(ValidationConfiguration configuration, String propertyName,
            Annotation annotation) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (isConstraintAnnotation(annotationType)) {
            ValidationRuleBuilder<ValidationRule> builder = (ValidationRuleBuilder<ValidationRule>) this.ruleBuilders
                    .get(annotationType);
            if (builder != null) {
                Class<? extends ValidationRule> ruleClass = ClassUtil.getActualGenericType(builder.getClass(),
                        ValidationRuleBuilder.class, 0);
                ValidationRule rule = configuration.getRule(propertyName, ruleClass);
                if (rule == null) {
                    rule = builder.create(annotation);
                    if (rule != null && rule.isValid()) {
                        configuration.addRule(propertyName, rule);
                    }
                } else {
                    builder.update(annotation, rule);
                }
            }
        }
    }
}
