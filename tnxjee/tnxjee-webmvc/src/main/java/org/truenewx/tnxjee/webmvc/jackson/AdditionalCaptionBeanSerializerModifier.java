package org.truenewx.tnxjee.webmvc.jackson;

import java.util.*;

import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.enums.EnumItemKey;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.model.annotation.ComponentType;
import org.truenewx.tnxjee.model.validation.constraint.RegionCode;
import org.truenewx.tnxjee.service.spec.region.RegionSource;
import org.truenewx.tnxjee.webmvc.util.AdditionalCaptionUtil;
import org.truenewx.tnxjee.webmvc.util.BeanPropertyMeta;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * 附加显示名称的Bean序列化修改器
 */
public class AdditionalCaptionBeanSerializerModifier extends BeanSerializerModifier {

    private EnumDictResolver enumDictResolver;
    private RegionSource regionSource;
    private Map<Class<?>, Collection<String>> ignoredPropertyNamesMapping = new HashMap<>();

    public AdditionalCaptionBeanSerializerModifier(ApplicationContext context) {
        this.enumDictResolver = context.getBean(EnumDictResolver.class); // 一定有
        this.regionSource = SpringUtil.getFirstBeanByClass(context, RegionSource.class); // 可能没有
    }

    public void addIgnoredPropertiesNames(Class<?> beanClass, String... ignoredPropertyNames) {
        if (ignoredPropertyNames.length > 0) {
            Collection<String> names = this.ignoredPropertyNamesMapping
                    .computeIfAbsent(beanClass, k -> new HashSet<>());
            CollectionUtil.addAll(names, ignoredPropertyNames);
        }
    }

    private boolean isIgnored(Object bean, String propertyName) {
        if (bean != null) {
            Collection<String> ignoredPropertyNames = this.ignoredPropertyNamesMapping.get(bean.getClass());
            return ignoredPropertyNames != null && ignoredPropertyNames.contains(propertyName);
        }
        return false;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
            List<BeanPropertyWriter> beanProperties) {
        for (int i = 0; i < beanProperties.size(); i++) {
            BeanPropertyWriter writer = beanProperties.get(i);
            BeanPropertyMeta meta = getPropertyMeta(writer);
            if (meta != null) {
                beanProperties.set(i, new BeanPropertyWriter(writer) {

                    private static final long serialVersionUID = 6267157125639776096L;

                    @Override
                    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov)
                            throws Exception {
                        super.serializeAsField(bean, gen, prov);

                        String propertyName = meta.getName();
                        if (!isIgnored(bean, propertyName)) {
                            Object rawValue = getMember().getValue(bean);
                            Object caption = AdditionalCaptionUtil.getAdditionalCaption(meta, rawValue,
                                    AdditionalCaptionBeanSerializerModifier.this.enumDictResolver,
                                    AdditionalCaptionBeanSerializerModifier.this.regionSource, prov.getLocale());
                            if (caption != null) {
                                String captionPropertyName = AdditionalCaptionUtil
                                        .getAdditionalCaptionPropertyName(propertyName);
                                if (caption instanceof String) {
                                    gen.writeStringField(captionPropertyName, (String) caption);
                                } else {
                                    gen.writeObjectField(captionPropertyName, caption);
                                }
                            }
                        }
                    }

                });
            }
        }
        return beanProperties;
    }

    private BeanPropertyMeta getPropertyMeta(BeanPropertyWriter writer) {
        JavaType propertyType = writer.getType();
        Class<?> rawClass = propertyType.getRawClass();
        boolean multi = false;
        if (rawClass.isArray()) {
            rawClass = rawClass.getComponentType();
            multi = true;
        } else if (Collection.class.isAssignableFrom(rawClass)) {
            ComponentType componentType = writer.getAnnotation(ComponentType.class);
            if (componentType == null) { // 集合属性没有配置@ComponentType注解，无法获知元素类型，则忽略
                return null;
            }
            rawClass = componentType.value();
            multi = true;
        }
        if (rawClass.isEnum()) {
            return new BeanPropertyMeta(writer.getName(), multi, rawClass);
        } else if (rawClass == String.class) {
            EnumItemKey enumItemKey = writer.getAnnotation(EnumItemKey.class);
            if (enumItemKey != null) {
                return new BeanPropertyMeta(writer.getName(), multi, enumItemKey);
            }
            if (this.regionSource != null) {
                RegionCode regionCode = writer.getAnnotation(RegionCode.class);
                if (regionCode != null) {
                    return new BeanPropertyMeta(writer.getName(), multi, regionCode);
                }
            }
        }
        return null;
    }

}
