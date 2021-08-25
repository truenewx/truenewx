package org.truenewx.tnxjee.webmvc.util;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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
 * 附加显示名称的工具类
 */
public class AdditionalCaptionUtil {

    private AdditionalCaptionUtil() {
    }

    public static Object getAdditionalCaption(BeanPropertyMeta meta, Object rawValue,
            EnumDictResolver enumDictResolver, @Nullable RegionSource regionSource, Locale locale) {
        if (meta.isMulti()) {
            Map<String, Object> captionMap = new HashMap<>();
            if (rawValue instanceof Object[]) {
                Object[] array = (Object[]) rawValue;
                for (Object element : array) {
                    if (element != null) {
                        String caption = getSingleAdditionalCaption(meta.getRawClass(), meta.getAnnotation(), element,
                                enumDictResolver, regionSource, locale);
                        if (caption != null) {
                            captionMap.put(element.toString(), caption);
                        }
                    }
                }
            } else if (rawValue instanceof Collection) {
                Collection<?> collection = (Collection<?>) rawValue;
                for (Object element : collection) {
                    if (element != null) {
                        String caption = getSingleAdditionalCaption(meta.getRawClass(), meta.getAnnotation(), element,
                                enumDictResolver, regionSource, locale);
                        if (caption != null) {
                            captionMap.put(element.toString(), caption);
                        }
                    }
                }
            }
            return captionMap.isEmpty() ? null : captionMap;
        } else {
            return getSingleAdditionalCaption(meta.getRawClass(), meta.getAnnotation(), rawValue, enumDictResolver,
                    regionSource, locale);
        }
    }

    // rawValue可能为null，无法获取其类型，所以必须传入rawClass
    private static String getSingleAdditionalCaption(Class<?> rawClass, Annotation annotation, Object rawValue,
            EnumDictResolver enumDictResolver, @Nullable RegionSource regionSource, Locale locale) {
        String caption = null;
        if (rawClass.isEnum()) {
            Enum<?> value = (Enum<?>) rawValue;
            if (value != null) {
                caption = enumDictResolver.getText(value, locale);
            }
            if (caption == null) {
                caption = Strings.EMPTY;
            }
        } else if (rawClass == String.class) {
            if (annotation instanceof EnumItemKey) {
                EnumItemKey enumItemKey = (EnumItemKey) annotation;
                String value = (String) rawValue;
                if (value != null) {
                    caption = enumDictResolver.getText(enumItemKey.type(), enumItemKey.subtype(), value, locale);
                }
                if (caption == null) {
                    caption = Strings.EMPTY;
                }
            }
            if (regionSource != null && annotation instanceof RegionCode) {
                RegionCode regionCode = (RegionCode) annotation;
                String value = (String) rawValue;
                if (StringUtils.isNotBlank(value)) {
                    Region region = regionSource.getRegion(value, locale);
                    if (region != null) {
                        caption = region.getCaption(regionCode.withSuffix());
                        if (caption != null) {
                            StringBuilder sb = new StringBuilder(caption);
                            Region parentRegion = region.getParent();
                            while (parentRegion != null && parentRegion.getLevel() >= regionCode.captionBeginLevel()) {
                                String parentCaption = parentRegion.getCaption(regionCode.withSuffix());
                                if (parentCaption != null) {
                                    sb.insert(0, parentCaption);
                                }
                                parentRegion = parentRegion.getParent();
                            }
                            caption = sb.toString();
                        }
                    }
                }
                if (caption == null) {
                    caption = Strings.EMPTY;
                }
            }
        }
        return caption;
    }

    public static String getAdditionalCaptionPropertyName(String rawPropertyName) {
        return rawPropertyName + Strings.UNDERLINE + "caption";
    }

    /**
     * 抽取指定bean中的所有附加显示名称字段为Map<br>
     *
     * @param bean Bean对象
     * @return 附加显示名称Map
     */
    public static Map<String, Object> toAdditionCaptionMap(Object bean) {
        if (bean == null) {
            return null;
        }
        HttpServletRequest request = SpringWebContext.getRequest();
        ApplicationContext context = SpringWebMvcUtil.getApplicationContext(request);
        EnumDictResolver enumDictResolver = context.getBean(EnumDictResolver.class); // 一定有
        RegionSource regionSource = SpringUtil.getFirstBeanByClass(context, RegionSource.class); // 可能没有
        Locale locale = request == null ? null : request.getLocale();

        Map<String, Object> map = new LinkedHashMap<>();
        Class<?> beanClass = bean.getClass();
        ClassUtil.loopPropertyDescriptors(beanClass, null, pd -> {
            BeanPropertyMeta meta = getPropertyMeta(beanClass, pd);
            if (meta != null) {
                try {
                    Object rawValue = pd.getReadMethod().invoke(bean);
                    Object caption = getAdditionalCaption(meta, rawValue, enumDictResolver, regionSource, locale);
                    if (caption != null) {
                        String propertyName = pd.getName();
                        map.put(propertyName, rawValue);
                        String captionPropertyName = getAdditionalCaptionPropertyName(propertyName);
                        map.put(captionPropertyName, caption);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LogUtil.error(AdditionalCaptionUtil.class, e);
                }
            }
            return true;
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
                ComponentType componentType = getAnnotation(beanClass, propertyName, ComponentType.class);
                if (componentType == null) { // 集合属性没有配置@ComponentType注解，无法获知元素类型，则忽略
                    return null;
                }
                rawClass = componentType.value();
                multi = true;
            }
            if (rawClass.isEnum()) {
                return new BeanPropertyMeta(propertyName, multi, rawClass);
            } else if (rawClass == String.class) {
                EnumItemKey enumItemKey = getAnnotation(beanClass, propertyName, EnumItemKey.class);
                if (enumItemKey != null) {
                    return new BeanPropertyMeta(propertyName, multi, enumItemKey);
                }
                RegionCode regionCode = getAnnotation(beanClass, propertyName, RegionCode.class);
                if (regionCode != null) {
                    return new BeanPropertyMeta(propertyName, multi, regionCode);
                }
            }
        }
        return null;
    }

    public static <A extends Annotation> A getAnnotation(Class<?> beanClass, String propertyName,
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
                return getAnnotation(beanClass, propertyName, annotationClass);
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
