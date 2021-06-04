package org.truenewx.tnxjee.service.feign;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.annotation.RequestParamIgnore;

import feign.Param;
import feign.QueryMapEncoder;
import feign.codec.EncodeException;

/**
 * 基于Bean属性Getter方法的查询参数映射编码器，改造自{@link feign.querymap.BeanQueryMapEncoder}
 */
public class BeanPropertyQueryMapEncoder implements QueryMapEncoder {

    private final Map<Class<?>, ObjectParamMetadata> classToMetadata = new HashMap<>();

    @Override
    public Map<String, Object> encode(Object object) throws EncodeException {
        try {
            ObjectParamMetadata metadata = getMetadata(object.getClass());
            Map<String, Object> propertyNameToValue = new HashMap<>();
            for (PropertyDescriptor pd : metadata.objectProperties) {
                Method method = pd.getReadMethod();
                Object value = method.invoke(object);
                if (value != null && value != object) {
                    Param alias = method.getAnnotation(Param.class);
                    String name = alias != null ? alias.value() : pd.getName();
                    propertyNameToValue.put(name, value);
                }
            }
            return propertyNameToValue;
        } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
            throw new EncodeException("Failure encoding object into query map", e);
        }
    }

    private ObjectParamMetadata getMetadata(Class<?> objectType) throws IntrospectionException {
        ObjectParamMetadata metadata = this.classToMetadata.get(objectType);
        if (metadata == null) {
            metadata = ObjectParamMetadata.parseObjectType(objectType);
            this.classToMetadata.put(objectType, metadata);
        }
        return metadata;
    }

    private static class ObjectParamMetadata {

        private final List<PropertyDescriptor> objectProperties;

        private ObjectParamMetadata(List<PropertyDescriptor> objectProperties) {
            this.objectProperties = Collections.unmodifiableList(objectProperties);
        }

        private static ObjectParamMetadata parseObjectType(Class<?> type) throws IntrospectionException {
            List<PropertyDescriptor> properties = new ArrayList<>();

            for (PropertyDescriptor pd : Introspector.getBeanInfo(type).getPropertyDescriptors()) {
                boolean isGetterMethod = pd.getReadMethod() != null && !"class".equals(pd.getName());
                if (isGetterMethod && contains(type, pd)) {
                    properties.add(pd);
                }
            }

            return new ObjectParamMetadata(properties);
        }

        private static boolean contains(Class<?> type, PropertyDescriptor pd) {
            // Getter方法被Json序列化忽略，则视为不包含
            if (pd.getReadMethod().getAnnotation(RequestParamIgnore.class) != null) {
                return false;
            }
            Field field = ClassUtil.findField(type, pd.getName());
            // 声明字段被Json序列化忽略，则视为不包含
            return field == null || field.getAnnotation(RequestParamIgnore.class) == null;
        }
    }
}
