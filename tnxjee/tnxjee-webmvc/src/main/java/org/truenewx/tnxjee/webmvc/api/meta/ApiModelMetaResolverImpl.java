package org.truenewx.tnxjee.webmvc.api.meta;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.*;

import javax.validation.constraints.Email;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.enums.EnumType;
import org.truenewx.tnxjee.core.enums.annotation.EnumSub;
import org.truenewx.tnxjee.core.i18n.PropertyCaptionResolver;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.Model;
import org.truenewx.tnxjee.model.validation.annotation.ReferenceConstraint;
import org.truenewx.tnxjee.model.validation.config.ValidationConfiguration;
import org.truenewx.tnxjee.model.validation.config.ValidationConfigurationFactory;
import org.truenewx.tnxjee.model.validation.rule.ValidationRule;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiModelPropertyMeta;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiModelPropertyType;
import org.truenewx.tnxjee.webmvc.validation.rule.mapper.ValidationRuleMapper;

/**
 * 属性校验规则生成器实现
 *
 * @author jianglei
 */
@Component
public class ApiModelMetaResolverImpl implements ApiModelMetaResolver, ContextInitializedBean {

    private static final Class<?>[] INTEGER_CLASSES = { long.class, int.class, short.class, byte.class, Long.class,
            Integer.class, Short.class, Byte.class, BigInteger.class };
    private static final Class<?>[] DECIMAL_CLASSES = { double.class, float.class, Double.class, Float.class,
            BigDecimal.class };

    @Autowired(required = false) // 如果工程未依赖tnxjee-repo，则可能没有该bean
    private ValidationConfigurationFactory validationConfigurationFactory;
    @Autowired
    private EnumDictResolver enumDictResolver;
    @Autowired
    private PropertyCaptionResolver propertyCaptionResolver;
    private Map<Class<?>, ValidationRuleMapper<ValidationRule>> ruleMappers = new HashMap<>();

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void afterInitialized(ApplicationContext context) throws Exception {
        Map<String, ValidationRuleMapper> beans = context.getBeansOfType(ValidationRuleMapper.class);
        for (ValidationRuleMapper<ValidationRule> ruleGenerator : beans.values()) {
            Class<?> ruleClass = ClassUtil.getActualGenericType(ruleGenerator.getClass(), ValidationRuleMapper.class,
                    0);
            this.ruleMappers.put(ruleClass, ruleGenerator);
        }
    }

    @Override
    @Cacheable("ApiModelMeta")
    public Map<String, ApiModelPropertyMeta> resolve(Class<? extends Model> modelClass, Locale locale) {
        Map<String, ApiModelPropertyMeta> metas = new HashMap<>();
        if (this.validationConfigurationFactory != null) {
            addMetas(metas, modelClass, locale, null, null);
        }
        return metas;
    }

    private void addMetas(Map<String, ApiModelPropertyMeta> metas, Class<? extends Model> modelClass, Locale locale,
            String propertyNamePrefix, String[] validPropertyNames) {
        // 限制了属性名清单，但清单为空，则无需进行任何处理，直接返回
        if (validPropertyNames != null && validPropertyNames.length == 0) {
            return;
        }
        ValidationConfiguration configuration = this.validationConfigurationFactory.getConfiguration(modelClass);
        ClassUtil.loopDynamicFields(modelClass, field -> {
            Class<?> fieldType = field.getType();
            String propertyName = field.getName();
            // 未限制属性名或属性名在限制清单内，才添加属性元数据
            if (validPropertyNames == null || ArrayUtils.contains(validPropertyNames, propertyName)) {
                if (Model.class.isAssignableFrom(fieldType)) { // 属性为模型类型，则通过引用约束注解获取元数据
                    ReferenceConstraint referenceConstraint = field.getAnnotation(ReferenceConstraint.class);
                    if (referenceConstraint != null) {
                        addMetas(metas, fieldType.asSubclass(Model.class), locale, propertyName + Strings.DOT,
                                referenceConstraint.value());
                    }
                } else { // 属性为其它类型的，直接获取元数据
                    String caption = this.propertyCaptionResolver.resolveCaption(modelClass, propertyName, locale);
                    if (propertyName.equals(caption) && !Locale.ENGLISH.getLanguage().equals(locale.getLanguage())) {
                        caption = null;
                    }
                    ApiModelPropertyType type = getType(field);
                    ApiModelPropertyMeta meta = new ApiModelPropertyMeta(caption, type);
                    Map<String, Object> validation = getValidation(configuration, propertyName, locale);
                    if (validation.size() > 0) {
                        meta.setValidation(validation);
                    }
                    if (fieldType.isEnum()) {
                        EnumSub enumSub = field.getAnnotation(EnumSub.class);
                        String subtype = enumSub == null ? null : enumSub.value();
                        EnumType enumType = this.enumDictResolver.getEnumType(fieldType.getName(), subtype, locale);
                        if (enumType != null) {
                            meta.setEnums(enumType.getItems());
                        }
                    }
                    String key = propertyNamePrefix == null ? propertyName : propertyNamePrefix + propertyName;
                    metas.put(key, meta);
                }
            }
            return true;
        });
    }

    private ApiModelPropertyType getType(Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType == String.class) {
            if (field.getAnnotation(Email.class) != null) {
                return ApiModelPropertyType.EMAIL;
            }
            if (field.getAnnotation(URL.class) != null) {
                return ApiModelPropertyType.EMAIL;
            }
            return ApiModelPropertyType.TEXT;
        } else {
            if (fieldType == boolean.class || fieldType == Boolean.class) {
                return ApiModelPropertyType.BOOLEAN;
            }
            if (ArrayUtils.contains(INTEGER_CLASSES, fieldType)) {
                return ApiModelPropertyType.INTEGER;
            }
            if (ArrayUtils.contains(DECIMAL_CLASSES, fieldType)) {
                return ApiModelPropertyType.DECIMAL;
            }
            if (fieldType.isArray()) { // 字符串数组或枚举数组为多选选项型
                Class<?> componentType = fieldType.getComponentType();
                if (componentType == String.class || componentType.isArray()) {
                    return ApiModelPropertyType.OPTION;
                }
            }
            if (LocalDate.class.isAssignableFrom(fieldType)) {
                return ApiModelPropertyType.DATE;
            }
            if (LocalTime.class.isAssignableFrom(fieldType)) {
                return ApiModelPropertyType.TIME;
            }
            // 其它日期和时间类型均为日期时间型
            if (Temporal.class.isAssignableFrom(fieldType) || Date.class.isAssignableFrom(fieldType)) {
                return ApiModelPropertyType.DATETIME;
            }
            // 枚举为单选选项型
            if (fieldType.isEnum()) {
                return ApiModelPropertyType.OPTION;
            }
        }
        return null;
    }

    private Map<String, Object> getValidation(ValidationConfiguration configuration, String propertyName,
            Locale locale) {
        Map<String, Object> validation = new LinkedHashMap<>(); // 保留顺序
        Set<ValidationRule> rules = configuration.getRules(propertyName);
        for (ValidationRule rule : rules) {
            ValidationRuleMapper<ValidationRule> ruleMapper = this.ruleMappers.get(rule.getClass());
            if (ruleMapper != null) {
                Map<String, Object> ruleMap = ruleMapper.toMap(rule, locale);
                if (ruleMap != null) {
                    validation.putAll(ruleMap);
                }
            }
        }
        return validation;
    }
}
