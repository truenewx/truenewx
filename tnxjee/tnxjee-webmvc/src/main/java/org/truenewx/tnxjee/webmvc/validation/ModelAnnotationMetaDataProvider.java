package org.truenewx.tnxjee.webmvc.validation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.ConstraintCreationContext;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.BeanMetaDataManagerImpl;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.core.MetaConstraints;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.FieldConstraintLocation;
import org.hibernate.validator.internal.metadata.provider.AnnotationMetaDataProvider;
import org.hibernate.validator.internal.metadata.provider.MetaDataProvider;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedField;
import org.hibernate.validator.internal.properties.javabean.JavaBeanField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.validation.annotation.InheritConstraint;

/**
 * 从模型的属性注解获取校验规则的元数据提供者<br>
 * 使得控制层得以校验模型中的字段约束，值得注意的是，这里并不包含ORM层的字段约束校验，后者交给数据层执行。<br>
 * 这样控制层校验命令模型和实体的字段约束，数据层校验实体和ORM的字段约束
 */
@Component
// TODO 注意：本类大量使用反射机制，越权访问框架类内部的属性，不是值得推荐的方式，后续依赖的底层框架版本更新时，需留意是否需要同步更新
public class ModelAnnotationMetaDataProvider implements MetaDataProvider {

    private AnnotationMetaDataProvider delegate;
    private ConstraintCreationContext constraintCreationContext;

    @Autowired
    public ModelAnnotationMetaDataProvider(LocalValidatorFactoryBean validator) {
        // 将自身介入字段校验体系中
        ValidatorImpl targetValidator = BeanUtil.getFieldValue(validator, "targetValidator");
        BeanMetaDataManager beanMetaDataManager = BeanUtil.getFieldValue(targetValidator, "beanMetaDataManager");
        if (beanMetaDataManager instanceof BeanMetaDataManagerImpl) {
            List<MetaDataProvider> metaDataProviders = BeanUtil.getFieldValue(beanMetaDataManager, "metaDataProviders");
            for (int i = 0; i < metaDataProviders.size(); i++) {
                MetaDataProvider metaDataProvider = metaDataProviders.get(i);
                if (metaDataProvider instanceof AnnotationMetaDataProvider) {
                    this.delegate = (AnnotationMetaDataProvider) metaDataProvider;
                    metaDataProviders.set(i, this);
                    this.constraintCreationContext = BeanUtil.getFieldValue(this.delegate, "constraintCreationContext");
                    break;
                }
            }
        }
    }

    @Override
    public AnnotationProcessingOptions getAnnotationProcessingOptions() {
        return this.delegate.getAnnotationProcessingOptions();
    }

    @Override
    public <T> BeanConfiguration<? super T> getBeanConfiguration(Class<T> beanClass) {
        BeanConfiguration<? super T> configuration = this.delegate.getBeanConfiguration(beanClass);
        if (beanClass != CommandModel.class && CommandModel.class.isAssignableFrom(beanClass)) { // 仅处理命令模型
            Class<?> entityClass = ClassUtil.getActualGenericType(beanClass, CommandModel.class, 0);
            if (entityClass != null) { // 将命令模型对应的实体类型中的属性约束注解规则合并至命令模型的属性约束注解规则中
                BeanConfiguration<?> entityConfiguration = this.delegate.getBeanConfiguration(entityClass);
                for (ConstrainedElement constrainedElement : configuration.getConstrainedElements()) {
                    if (constrainedElement instanceof ConstrainedField) { // 为简化计，仅考虑字段上的约束
                        ConstrainedField constrainedField = (ConstrainedField) constrainedElement;
                        Field field = getField(constrainedField);
                        if (field != null) {
                            Set<MetaConstraint<?>> entityFieldConstraints = getFieldMetaConstraints(entityConfiguration,
                                    field);
                            if (CollectionUtils.isNotEmpty(entityFieldConstraints)) { // 存在有对应的实体类型字段约束才需要合并
                                Set<MetaConstraint<?>> constraints = new HashSet<>(constrainedField.getConstraints());
                                for (MetaConstraint<?> constraint : entityFieldConstraints) {
                                    // 从实体类型得到的字段约束不能直接使用，需复制一份后再修改其中的字段定位，才可用于命令模型字段
                                    // 否则会改动实体类型的字段约束，导致数据层校验失效
                                    ConstraintDescriptorImpl<?> constraintDescriptor = constraint.getDescriptor();
                                    try {
                                        Constructor<FieldConstraintLocation> constructor = FieldConstraintLocation.class
                                                .getDeclaredConstructor(Field.class);
                                        constructor.setAccessible(true);
                                        FieldConstraintLocation location = constructor.newInstance(field);
                                        constraints.add(MetaConstraints.create(
                                                this.constraintCreationContext.getTypeResolutionHelper(),
                                                this.constraintCreationContext.getValueExtractorManager(),
                                                this.constraintCreationContext.getConstraintValidatorManager(),
                                                constraintDescriptor, location));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                BeanUtil.setFieldValue(constrainedField, "constraints", constraints);
                            }
                        }
                    }
                }
            }
        }
        return configuration;
    }

    private Field getField(ConstrainedField constrainedField) {
        org.hibernate.validator.internal.properties.Field propertyField = constrainedField.getField();
        if (propertyField instanceof JavaBeanField) {
            return BeanUtil.getFieldValue(propertyField, "field");
        }
        return null;
    }

    private Set<MetaConstraint<?>> getFieldMetaConstraints(BeanConfiguration<?> configuration, Field field) {
        String fieldName = field.getName();
        InheritConstraint inheritConstraint = field.getAnnotation(InheritConstraint.class);
        if (inheritConstraint != null) { // 考虑约束继承
            Class<? extends Entity> inheritedEntityClass = inheritConstraint.type();
            if (inheritedEntityClass != Entity.class && inheritedEntityClass != configuration.getBeanClass()) {
                configuration = this.delegate.getBeanConfiguration(inheritedEntityClass);
            }
            String inheritedFieldName = inheritConstraint.value();
            if (StringUtils.isNotBlank(inheritedFieldName)) {
                fieldName = inheritedFieldName;
            }
        }
        for (ConstrainedElement constrainedElement : configuration.getConstrainedElements()) {
            if (constrainedElement instanceof ConstrainedField) {
                ConstrainedField constrainedField = (ConstrainedField) constrainedElement;
                if (fieldName.equals(constrainedField.getField().getName())) {
                    return constrainedField.getConstraints();
                }
            }
        }
        return null;
    }

}
