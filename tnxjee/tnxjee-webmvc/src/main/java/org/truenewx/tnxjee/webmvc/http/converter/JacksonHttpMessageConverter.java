package org.truenewx.tnxjee.webmvc.http.converter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.jackson.TypedPropertyFilter;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.JacksonUtil;
import org.truenewx.tnxjee.core.util.PropertyMeta;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.webmvc.http.annotation.ResultFilter;
import org.truenewx.tnxjee.webmvc.jackson.AttachFieldBeanSerializerModifier;
import org.truenewx.tnxjee.webmvc.jackson.JsonWithClassProperty;
import org.truenewx.tnxjee.webmvc.servlet.mvc.method.HandlerMethodMapping;
import org.truenewx.tnxjee.webmvc.util.WebMvcUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * 基于Jackson实现的HTTP消息JSON转换器
 */
@Component
public class JacksonHttpMessageConverter extends MappingJackson2HttpMessageConverter implements InitializingBean {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;
    @Autowired
    private ApplicationContext context;

    private final Map<String, ObjectMapper> mappers = new HashMap<>();
    private final ObjectMapper defaultInternalMapper = JacksonUtil.copyDefaultMapper();
    private final ObjectMapper defaultExternalMapper = JacksonUtil.copyDefaultMapper();

    public JacksonHttpMessageConverter() {
        super(JacksonUtil.copyNonConcreteClassedMapper()); // 默认映射器实际上作为读取器，针对非具化类型读取类型字段
        setDefaultCharset(StandardCharsets.UTF_8);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 默认外部映射器需要附加字段输出能力
        withSerializerModifier(this.defaultExternalMapper, new AttachFieldBeanSerializerModifier(this.context));
    }

    private void withSerializerModifier(ObjectMapper mapper, BeanSerializerModifier modifier) {
        mapper.setSerializerFactory(mapper.getSerializerFactory().withSerializerModifier(modifier));
    }

    private String getMapperKey(boolean internal, Method method) {
        return (internal ? "in:" : "ex:") + method.toString();
    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        HttpServletRequest request = SpringWebContext.getRequest();
        if (request != null) {
            HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(request);
            if (handlerMethod != null) {
                Method method = handlerMethod.getMethod();
                boolean internal = WebMvcUtil.isInternalRpc(request);
                ObjectMapper mapper = getMapper(internal, method);
                String json = mapper.writeValueAsString(object);
                Charset charset = Objects.requireNonNullElse(getDefaultCharset(), StandardCharsets.UTF_8);
                IOUtils.write(json, outputMessage.getBody(), charset.name());
                return;
            }
        }
        super.writeInternal(object, type, outputMessage);
    }

    private ObjectMapper getMapper(boolean internal, Method method) {
        String mapperKey = getMapperKey(internal, method);
        ObjectMapper mapper = this.mappers.get(mapperKey);
        if (mapper == null) {
            // 存在结果过滤设置，或需要生成对象类型字段，则需构建方法特定的映射器
            ResultFilter[] resultFilters = method.getAnnotationsByType(ResultFilter.class);
            boolean classPropertyNeeded = isClassPropertyNeeded(internal, method);
            if (ArrayUtils.isNotEmpty(resultFilters) || classPropertyNeeded) {
                mapper = buildMapper(internal, method.getReturnType(), resultFilters, classPropertyNeeded);
                this.mappers.put(mapperKey, mapper);
            } else { // 取默认映射器
                mapper = internal ? this.defaultInternalMapper : this.defaultExternalMapper;
            }
        }
        return mapper;
    }

    private boolean isClassPropertyNeeded(boolean internal, Method method) {
        if (internal) { // 内部调用才可能需要构建输出类型字段的映射器
            // 方法上有@JsonWithClassProperty注解，则一定输出类型字段
            if (method.getAnnotation(JsonWithClassProperty.class) != null) {
                return true;
            }
            Class<?> returnType = method.getReturnType();
            // 结果类型为非具化类型，则需要构建输出类型字段的映射器
            if (isClassPropertyNeeded(returnType)) {
                return true;
            }
            Collection<PropertyMeta> metas = ClassUtil.findPropertyMetas(returnType, true, false, true, null);
            for (PropertyMeta meta : metas) {
                if (!meta.containsAnnotation(JsonIgnore.class)) {
                    // 需要序列化的属性标注了@JsonWithClassProperty注解，则一定输出类型字段
                    if (meta.containsAnnotation(JsonWithClassProperty.class)) {
                        return true;
                    }
                    // 需要序列化的属性中包含非具化类型，则需要构建输出类型字段的映射器
                    Class<?> type = meta.getType();
                    if (isClassPropertyNeeded(type)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isClassPropertyNeeded(Class<?> clazz) {
        // 运行期无法知晓集合中元素的声明类型，因此集合类型均可能需要附加类型属性
        if (Iterable.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (clazz.isArray()) { // 类型为数组，则检查其元素类型
            clazz = clazz.getComponentType();
        }
        return JacksonUtil.isNonConcrete(clazz);
    }

    private ObjectMapper buildMapper(boolean internal, Class<?> resultType, ResultFilter[] resultFilters,
            boolean classPropertyNeeded) {
        TypedPropertyFilter filter = new TypedPropertyFilter();
        AttachFieldBeanSerializerModifier attachFieldModifier = new AttachFieldBeanSerializerModifier(this.context);
        for (ResultFilter resultFilter : resultFilters) {
            Class<?> filteredType = resultFilter.type();
            if (filteredType == Object.class) {
                filteredType = resultType;
            }
            filter.addIncludedProperties(filteredType, resultFilter.included());
            filter.addExcludedProperties(filteredType, resultFilter.excluded());
            attachFieldModifier.addIgnoredPropertiesNames(filteredType, resultFilter.pureEnum());
        }
        // 被过滤的类型中如果不包含结果类型，则加入结果类型，以确保至少包含结果类型
        Class<?>[] filteredTypes = filter.getTypes();
        if (!ArrayUtils.contains(filteredTypes, resultType)) {
            filteredTypes = ArrayUtils.add(filteredTypes, resultType);
        }
        ObjectMapper mapper = JacksonUtil.buildMapper(filter, filteredTypes);
        if (classPropertyNeeded) { // 需要附加类型属性，则针对所有复合类型均附加类型属性输出能力，以适配集合属性中的复合类型元素
            JacksonUtil.withComplexClassProperty(mapper);
        }
        if (!internal) { // 外部映射器需要附加字段输出能力
            withSerializerModifier(mapper, attachFieldModifier);
        }
        return mapper;
    }

}
