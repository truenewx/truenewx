package org.truenewx.tnxjee.core.util;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;

/**
 * 属性Map
 */
public class AttributeMap extends AbstractMap<String, String> implements Attributes {

    private Map<String, String> map;
    private Map<Class<?>, Function<String, ?>> transfers = new HashMap<>();

    public AttributeMap(Map<String, String> map) {
        this.map = map;
        // 初始化转换函数
        addTransfer(Integer.class, s -> {
            return MathUtil.parseInteger(s);
        });
        addTransfer(Long.class, s -> {
            return MathUtil.parseLong(s);
        });
        addTransfer(Boolean.class, s -> {
            return Boolean.valueOf(s);
        });
        addTransfer(BigDecimal.class, s -> {
            return MathUtil.parseDecimal(s);
        });
        addTransfer(LocalDate.class, s -> {
            return TemporalUtil.parseDate(s);
        });
        addTransfer(LocalTime.class, s -> {
            return TemporalUtil.parseTime(s);
        });
        addTransfer(LocalDateTime.class, s -> {
            return TemporalUtil.parseDateTime(s);
        });
        addTransfer(Instant.class, s -> {
            return TemporalUtil.parseInstant(s);
        });
    }

    public AttributeMap() {
        this(new HashMap<>());
    }

    public <V> void addTransfer(Class<V> type, Function<String, V> transfer) {
        this.transfers.put(type, transfer);
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public String put(String key, String value) {
        return this.map.put(key, value);
    }

    @Override
    public String getAttribute(String key) {
        return this.map.get(key);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <V> V getAttribute(String key, V defaultValue) {
        String value = getAttribute(key);
        if (value == null) {
            return defaultValue;
        }
        // 此时value一定不为null
        if (defaultValue instanceof String) {
            return (V) value;
        }
        // 如果默认值类型不是字符串，则需要进行转换
        Class<?> targetClass = defaultValue.getClass();
        if (targetClass.isEnum()) {
            return (V) EnumUtils.getEnum((Class<Enum>) targetClass, value);
        } else {
            Function<String, V> transfer = (Function<String, V>) this.transfers.get(targetClass);
            if (transfer == null) { // 不支持的转换
                throw new UnsupportedOperationException("Can't transfer String to " + targetClass);
            }
            return transfer.apply(value);
        }
    }

    @Override
    public Map<String, String> getAttributes(String... excludedKeys) {
        Map<String, String> attributes = new HashMap<>();
        this.map.forEach((key, value) -> {
            if (!ArrayUtils.contains(excludedKeys, key)) {
                attributes.put(key, value);
            }
        });
        return attributes;
    }
}
