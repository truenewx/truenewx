package org.truenewx.tnxjee.core.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import org.truenewx.tnxjee.core.util.FilteredNames;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 类型区分的属性过滤器
 */
public class TypedPropertyFilter extends SimpleBeanPropertyFilter {

    private Map<Class<?>, FilteredNames> properties = new HashMap<>();

    private FilteredNames getFilteredProperties(Class<?> beanClass) {
        FilteredNames filteredProperties = this.properties.get(beanClass);
        if (filteredProperties == null) {
            filteredProperties = new FilteredNames();
            this.properties.put(beanClass, filteredProperties);
        }
        return filteredProperties;
    }

    public TypedPropertyFilter addIncludedProperties(Class<?> beanClass, String... includedProperties) {
        if (includedProperties.length > 0) {
            getFilteredProperties(beanClass).addIncluded(includedProperties);
        }
        return this;
    }

    public TypedPropertyFilter addExcludedProperties(Class<?> beanClass, String... excludedProperties) {
        if (excludedProperties.length > 0) {
            getFilteredProperties(beanClass).addExcluded(excludedProperties);
        }
        return this;
    }

    public TypedPropertyFilter addAllProperties(Map<Class<?>, FilteredNames> map) {
        this.properties.putAll(map);
        return this;
    }

    public Class<?>[] getTypes() {
        Set<Class<?>> types = this.properties.keySet();
        return types.toArray(new Class<?>[types.size()]);
    }


    @Override
    protected boolean include(PropertyWriter writer) {
        if (writer instanceof BeanTypeWarePropertyWriter) {
            BeanTypeWarePropertyWriter typeWriter = (BeanTypeWarePropertyWriter) writer;
            Class<?> beanType = typeWriter.getBeanType();
            String propertyName = writer.getName();
            return getFilteredProperties(beanType).include(propertyName);
        }
        return true;
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer)
            throws Exception {
        super.serializeAsField(pojo, jgen, provider, new BeanTypeWarePropertyWriter(writer, pojo.getClass()));
    }

}
