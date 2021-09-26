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
import org.truenewx.tnxjee.webmvc.http.annotation.ResultWithClassField;
import org.truenewx.tnxjee.webmvc.jackson.AttachFieldBeanSerializerModifier;
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

    private final Map<String, ObjectMapper> writers = new HashMap<>();
    private final ObjectMapper defaultInternalWriter = JacksonUtil.copyDefaultMapper();
    private final ObjectMapper defaultExternalWriter = JacksonUtil.copyDefaultMapper();

    public JacksonHttpMessageConverter() {
        super(JacksonUtil.copyNonConcreteClassedMapper()); // 默认映射器实际上作为了读取器，始终具有读取类型字段的能力
        setDefaultCharset(StandardCharsets.UTF_8);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 默认外部输出器需要附加字段输出能力
        withSerializerModifier(this.defaultExternalWriter, new AttachFieldBeanSerializerModifier(this.context));
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
                ObjectMapper mapper = getWriter(internal, method);
                String json = mapper.writeValueAsString(object);
                Charset charset = Objects.requireNonNullElse(getDefaultCharset(), StandardCharsets.UTF_8);
                IOUtils.write(json, outputMessage.getBody(), charset.name());
                return;
            }
        }
        super.writeInternal(object, type, outputMessage);
    }

    private ObjectMapper getWriter(boolean internal, Method method) {
        String mapperKey = getMapperKey(internal, method);
        ObjectMapper writer = this.writers.get(mapperKey);
        if (writer == null) {
            ResultFilter[] resultFilters = method.getAnnotationsByType(ResultFilter.class);
            if (ArrayUtils.isNotEmpty(resultFilters) || requiresBuildWriterWithClassProperty(internal, method)) {
                writer = buildWriter(internal, method.getReturnType(), resultFilters);
                this.writers.put(mapperKey, writer);
            } else { // 没有结果过滤设置的取默认映射器
                writer = internal ? this.defaultInternalWriter : this.defaultExternalWriter;
            }
        }
        return writer;
    }

    private boolean requiresBuildWriterWithClassProperty(boolean internal, Method method) {
        if (internal) { // 内部调用才可能需要构建输出类型字段的输出器
            // 方法上有@ResultWithClassField注解，则一定输出类型字段
            if (method.getAnnotation(ResultWithClassField.class) != null) {
                return true;
            }
            Collection<PropertyMeta> metas = ClassUtil.findPropertyMetas(method.getReturnType(), true, false, true,
                    null);
            for (PropertyMeta meta : metas) {
                // 需要序列化的属性中包含集合或可序列化的非具化类型，则需要构建输出类型字段的输出器
                if (!meta.containsAnnotation(JsonIgnore.class)) {
                    Class<?> type = meta.getType();
                    if (Iterable.class.isAssignableFrom(type) || ClassUtil.isSerializableNonConcrete(type)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private ObjectMapper buildWriter(boolean internal, Class<?> resultType, ResultFilter[] resultFilters) {
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
        ObjectMapper writer = JacksonUtil.buildMapper(filter, filteredTypes);
        if (internal) { // 内部输出器需要附加类型属性输出能力
            JacksonUtil.withNonConcreteClassProperty(writer);
        } else { // 外部输出器需要附加字段输出能力
            withSerializerModifier(writer, attachFieldModifier);
        }
        return writer;
    }

}
