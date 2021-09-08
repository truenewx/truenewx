package org.truenewx.tnxjee.webmvc.util;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.enums.annotation.EnumItemKey;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxjee.model.annotation.ComponentType;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.validation.annotation.InheritConstraint;
import org.truenewx.tnxjee.model.validation.constraint.RegionCode;
import org.truenewx.tnxjee.service.spec.region.Region;
import org.truenewx.tnxjee.service.spec.region.RegionSource;
import org.truenewx.tnxjee.web.context.SpringWebContext;

/**
 * 附加字段工具类
 */
public class AttachFieldUtil {

    private AttachFieldUtil() {
    }

    public static Object getAttachedCaptionValue(BeanPropertyMeta meta, Object rawValue,
            EnumDictResolver enumDictResolver, @Nullable RegionSource regionSource, Locale locale) {
        if (meta.isMulti()) {
            Map<String, Object> captionMap = new HashMap<>();
            if (rawValue instanceof Object[]) {
                Object[] array = (Object[]) rawValue;
                for (Object element : array) {
                    if (element != null) {
                        String caption = getAttachedCaptionOfSingle(element, meta.getAnnotation(), enumDictResolver,
                                regionSource, locale);
                        if (caption != null) {
                            captionMap.put(element.toString(), caption);
                        }
                    }
                }
            } else if (rawValue instanceof Collection) {
                Collection<?> collection = (Collection<?>) rawValue;
                for (Object element : collection) {
                    if (element != null) {
                        String caption = getAttachedCaptionOfSingle(element, meta.getAnnotation(), enumDictResolver,
                                regionSource, locale);
                        if (caption != null) {
                            captionMap.put(element.toString(), caption);
                        }
                    }
                }
            }
            return captionMap.isEmpty() ? null : captionMap;
        } else {
            return getAttachedCaptionOfSingle(rawValue, meta.getAnnotation(), enumDictResolver, regionSource, locale);
        }
    }

    private static String getAttachedCaptionOfSingle(Object rawValue, Annotation annotation,
            EnumDictResolver enumDictResolver, @Nullable RegionSource regionSource, Locale locale) {
        String caption = null;
        boolean resolved = false;
        if (rawValue != null) { // null值一定没有附加的显示名称
            if (rawValue instanceof Enum) {
                Enum<?> value = (Enum<?>) rawValue;
                caption = enumDictResolver.getText(value, locale);
                resolved = true;
            } else if (rawValue instanceof String) {
                String value = (String) rawValue;
                if (annotation instanceof EnumItemKey) {
                    EnumItemKey enumItemKey = (EnumItemKey) annotation;
                    caption = enumDictResolver.getText(enumItemKey.type(), enumItemKey.subtype(), value, locale);
                    resolved = true;
                } else if (annotation instanceof RegionCode) {
                    RegionCode regionCode = (RegionCode) annotation;
                    if (regionSource != null && StringUtils.isNotBlank(value)) {
                        Region region = regionSource.getRegion(value, locale);
                        if (region != null) {
                            caption = region.getCaption(regionCode.withSuffix());
                            if (caption != null) {
                                StringBuilder sb = new StringBuilder(caption);
                                Region parentRegion = region.getParent();
                                while (parentRegion != null
                                        && parentRegion.getLevel() >= regionCode.captionBeginLevel()) {
                                    String parentCaption = parentRegion.getCaption(regionCode.withSuffix());
                                    if (parentCaption != null) {
                                        sb.insert(0, parentCaption);
                                    }
                                    parentRegion = parentRegion.getParent();
                                }
                                caption = sb.toString();
                            }
                            resolved = true;
                        }
                    }
                }
            }
        }
        // 确保已经处理的结果不为null
        if (caption == null && resolved) {
            caption = Strings.EMPTY;
        }
        return caption;
    }

    public static String getAttachedFieldName(String rawPropertyName, String attachedPropertyName) {
        return rawPropertyName + Strings.UNDERLINE + attachedPropertyName;
    }

    public static String getAttachedCaptionFieldName(String rawPropertyName) {
        return getAttachedFieldName(rawPropertyName, "caption");
    }

    /**
     * 遍历单值属性（非数组）的附加字段
     *
     * @param meta     属性名
     * @param rawValue 属性值
     * @param consumer 附加字段消费者
     */
    @SuppressWarnings("unchecked")
    public static void loopAttachedField(BeanPropertyMeta meta, Object rawValue, BiConsumer<String, Object> consumer) {
        Class<?> rawClass = meta.getRawClass();
        if (rawClass.isEnum()) { // 枚举或枚举集合附加枚举中的额外属性
            String rawPropertyName = meta.getName();
            ClassUtil.loopSimplePropertyDescriptors(rawClass, pd -> {
                Method readMethod = pd.getReadMethod();
                if (readMethod != null) {
                    Object attachedFieldValue = null;
                    if (meta.isMulti()) {
                        Map<String, Object> attachedMap = new HashMap<>();
                        if (rawValue instanceof Enum[]) {
                            Enum<?>[] array = (Enum<?>[]) rawValue;
                            for (Enum<?> element : array) {
                                if (element != null) {
                                    try {
                                        attachedMap.put(element.name(), readMethod.invoke(element));
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                        } else if (rawValue instanceof Collection) {
                            Collection<Enum<?>> collection = (Collection<Enum<?>>) rawValue;
                            for (Enum<?> element : collection) {
                                if (element != null) {
                                    try {
                                        attachedMap.put(element.name(), readMethod.invoke(element));
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                        }
                        if (attachedMap.size() > 0) {
                            attachedFieldValue = attachedMap;
                        }
                    } else {
                        try {
                            attachedFieldValue = readMethod.invoke(rawValue);
                        } catch (Exception ignored) {
                        }
                    }
                    if (attachedFieldValue != null) {
                        String attachedFieldName = AttachFieldUtil.getAttachedFieldName(rawPropertyName, pd.getName());
                        consumer.accept(attachedFieldName, attachedFieldValue);
                    }
                }
            });
        }

    }

    /**
     * 抽取指定bean中的所有附加字段为Map
     *
     * @param bean Bean对象
     * @return 附加字段Map
     */
    public static Map<String, Object> toAttachedMap(Object bean) {
        if (bean == null) {
            return null;
        }
        HttpServletRequest request = SpringWebContext.getRequest();
        if (request == null) {
            return null;
        }
        ApplicationContext context = SpringWebMvcUtil.getApplicationContext(request);
        EnumDictResolver enumDictResolver = context.getBean(EnumDictResolver.class); // 一定有
        RegionSource regionSource = SpringUtil.getFirstBeanByClass(context, RegionSource.class); // 可能没有
        Locale locale = request.getLocale();

        Map<String, Object> map = new LinkedHashMap<>();
        Class<?> beanClass = bean.getClass();
        ClassUtil.loopSimplePropertyDescriptors(beanClass, pd -> {
            BeanPropertyMeta meta = getPropertyMeta(beanClass, pd);
            if (meta != null) {
                try {
                    Object rawValue = pd.getReadMethod().invoke(bean);
                    String propertyName = pd.getName();
                    Object caption = getAttachedCaptionValue(meta, rawValue, enumDictResolver, regionSource, locale);
                    if (caption != null) {
                        map.put(propertyName, rawValue);
                        String captionFieldName = getAttachedCaptionFieldName(propertyName);
                        map.put(captionFieldName, caption);
                    }
                    // 添加除显示名称外的其它附加字段
                    loopAttachedField(meta, rawValue, map::put);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LogUtil.error(AttachFieldUtil.class, e);
                }
            }
        });
        return map;
    }

    private static BeanPropertyMeta getPropertyMeta(Class<?> beanClass, PropertyDescriptor pd) {
        if (pd.getReadMethod() != null) { // 有读取方法才可取值，才有效
            Class<?> rawClass = pd.getPropertyType();
            String propertyName = pd.getName();
            boolean multi = false;
            if (rawClass.isArray()) {
                rawClass = rawClass.getComponentType();
                multi = true;
            } else if (Collection.class.isAssignableFrom(rawClass)) {
                ComponentType componentType = findAnnotation(beanClass, propertyName, ComponentType.class);
                if (componentType == null) { // 集合属性没有配置@ComponentType注解，无法获知元素类型，则忽略
                    return null;
                }
                rawClass = componentType.value();
                multi = true;
            }
            if (rawClass.isEnum()) {
                return new BeanPropertyMeta(propertyName, multi, rawClass);
            } else if (rawClass == String.class) {
                EnumItemKey enumItemKey = findAnnotation(beanClass, propertyName, EnumItemKey.class);
                if (enumItemKey != null) {
                    return new BeanPropertyMeta(propertyName, multi, enumItemKey);
                }
                RegionCode regionCode = findAnnotation(beanClass, propertyName, RegionCode.class);
                if (regionCode != null) {
                    return new BeanPropertyMeta(propertyName, multi, regionCode);
                }
            }
        }
        return null;
    }

    public static <A extends Annotation> A findAnnotation(Class<?> beanClass, String propertyName,
            Class<A> annotationClass) {
        Method readMethod = ClassUtil.findPropertyMethod(beanClass, propertyName, true);
        A annotation = getAnnotation(readMethod, propertyName, annotationClass);
        if (annotation != null) {
            return annotation;
        }
        if (CommandModel.class.isAssignableFrom(beanClass)) {
            InheritConstraint ic = ClassUtil.findAnnotation(beanClass, propertyName, InheritConstraint.class);
            if (ic == null) {
                beanClass = ClassUtil.getActualGenericType(beanClass, CommandModel.class, 0);
            } else {
                if (ic.type() != Entity.class) {
                    beanClass = ic.type();
                }
                if (StringUtils.isNotBlank(ic.value())) {
                    propertyName = ic.value();
                }
            }
            if (beanClass != null) {
                return findAnnotation(beanClass, propertyName, annotationClass);
            }
        }
        return null;
    }

    private static <A extends Annotation> A getAnnotation(Method readMethod, String propertyName,
            Class<A> annotationClass) {
        if (readMethod != null) {
            A annotation = readMethod.getAnnotation(annotationClass);
            if (annotation == null) {
                annotation = ClassUtil.findAnnotation(readMethod.getDeclaringClass(), propertyName, annotationClass);
            }
            return annotation;
        }
        return null;
    }

}
