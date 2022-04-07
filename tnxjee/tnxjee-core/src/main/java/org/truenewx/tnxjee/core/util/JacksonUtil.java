package org.truenewx.tnxjee.core.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.function.Predicate;

import org.springframework.beans.BeanUtils;
import org.truenewx.tnxjee.core.jackson.CompositeLocalTimeDeserializer;
import org.truenewx.tnxjee.core.jackson.PredicateTypeResolverBuilder;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

/**
 * Jackson序列化工具类
 */
public class JacksonUtil {

    private JacksonUtil() {
    }

    public static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
    public static final ObjectMapper CLASSED_MAPPER;

    static {
        // 初始化JavaTimeModule
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //处理LocalDateTime
        DateTimeFormatter dateTimeFormatter = TemporalUtil.formatter(DateUtil.LONG_DATE_PATTERN);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        //处理LocalDate
        DateTimeFormatter dateFormatter = TemporalUtil.formatter(DateUtil.SHORT_DATE_PATTERN);
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        //处理LocalTime
        DateTimeFormatter timeFormatter = TemporalUtil.formatter(DateUtil.TIME_PATTERN);
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        DateTimeFormatter timeToMinuteFormatter = TemporalUtil.formatter(DateUtil.TIME_PATTERN_TO_MINUTE);
        javaTimeModule.addDeserializer(LocalTime.class,
                new CompositeLocalTimeDeserializer(timeFormatter, timeToMinuteFormatter));
        //注册时间模块
        DEFAULT_MAPPER.registerModule(javaTimeModule);

        DEFAULT_MAPPER.findAndRegisterModules();
        DEFAULT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 序列化时不输出null
        DEFAULT_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS); // 允许序列化空对象
        DEFAULT_MAPPER.enable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS); // 日期类型的Key转换为时间戳
        DEFAULT_MAPPER.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS); // 序列化日期类型不转换为纳秒而是毫秒
        DEFAULT_MAPPER.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS); // 反序列化日期类型不从纳秒而是毫秒转换
        DEFAULT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // 反序列化时允许未知属性

        // 默认的映射器初始化后再初始化带复合类型属性的映射器
        CLASSED_MAPPER = copyComplexClassedMapper();
    }

    public static ObjectMapper copyDefaultMapper() {
        return DEFAULT_MAPPER.copy();
    }

    /**
     * 复制一个将复合对象类型写入JSON串的映射器
     *
     * @return 将复合对象类型写入JSON串的映射器
     */
    public static ObjectMapper copyComplexClassedMapper() {
        return withComplexClassProperty(copyDefaultMapper());
    }

    /**
     * 复制一个将非具化对象类型写入JSON串的映射器
     *
     * @return 将非具化对象类型写入JSON串的映射器
     */
    public static ObjectMapper copyNonConcreteClassedMapper() {
        return withNonConcreteClassProperty(copyDefaultMapper());
    }

    @JsonFilter("DynamicFilter")
    private interface DynamicFilter {

    }

    public static ObjectMapper buildMapper(PropertyFilter filter, Class<?>... beanClasses) {
        ObjectMapper mapper = copyDefaultMapper();
        withPropertyFilter(filter, mapper, beanClasses);
        return mapper;
    }

    public static void withPropertyFilter(PropertyFilter filter, ObjectMapper mapper, Class<?>[] beanClasses) {
        if (filter != null && beanClasses.length > 0) {
            for (Class<?> beanClass : beanClasses) {
                registerFilterable(mapper, beanClass);
            }
            String filterId = DynamicFilter.class.getAnnotation(JsonFilter.class).value();
            FilterProvider filterProvider = new SimpleFilterProvider().addFilter(filterId, filter);
            mapper.setFilterProvider(filterProvider);
        }
    }

    private static void registerFilterable(ObjectMapper mapper, Class<?> beanClass) {
        if (mapper.addMixIn(beanClass, DynamicFilter.class) == null) { // 首次注册才考虑同时注册复合属性类型
            Collection<PropertyMeta> propertyMetas = ClassUtil.findPropertyMetas(beanClass, true, false, true,
                    (propertyType, propertyName) -> {
                        return ClassUtil.isComplex(propertyType);
                    });
            propertyMetas.forEach(meta -> {
                registerFilterable(mapper, meta.getType());
            });
        }
    }

    public static ObjectMapper withComplexClassProperty(ObjectMapper mapper) {
        return withClassProperty(mapper, type -> {
            return ClassUtil.isComplex(type.getRawClass());
        });
    }

    public static ObjectMapper withClassProperty(ObjectMapper mapper, Predicate<JavaType> predicate) {
        PredicateTypeResolverBuilder builder = new PredicateTypeResolverBuilder(predicate);
        builder.init(JsonTypeInfo.Id.CLASS, null).inclusion(JsonTypeInfo.As.PROPERTY);
        return mapper.setDefaultTyping(builder);
    }

    public static ObjectMapper withNonConcreteClassProperty(ObjectMapper mapper) {
        return withClassProperty(mapper, type -> isSerializableNonConcrete(type.getRawClass()));
    }

    /**
     * 判断是否可序列化的非具化类型
     *
     * @param clazz 类型
     * @return 是否可序列化的非具化类型
     */
    public static boolean isSerializableNonConcrete(Class<?> clazz) {
        try {
            if (clazz.isInterface()) { // 接口，需至少包含一个getter方法
                PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(clazz);
                for (PropertyDescriptor pd : pds) {
                    // 带参数的getter方法会形成属性类型为null的属性描述，应忽略
                    if (pd.getPropertyType() != null && pd.getReadMethod() != null) {
                        return true;
                    }
                }
            } else { // 非接口，必须公开且抽象（非抽象的为具化类型），并具有公开的无参构造函数，且至少包含一个具有getter方法的属性
                int modifiers = clazz.getModifiers();
                if (Modifier.isPublic(modifiers) && Modifier.isAbstract(modifiers)) {
                    clazz.getConstructor();
                    PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(clazz);
                    for (PropertyDescriptor pd : pds) {
                        if (pd.getPropertyType() != null && pd.getReadMethod() != null) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static String getTypePropertyName() {
        return JsonTypeInfo.Id.CLASS.getDefaultPropertyName();
    }

}
