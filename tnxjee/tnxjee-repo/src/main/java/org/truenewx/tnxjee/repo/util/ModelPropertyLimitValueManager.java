package org.truenewx.tnxjee.repo.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;

/**
 * 模型属性限制值管理器
 *
 * @author jianglei
 */
@Component
public class ModelPropertyLimitValueManager {

    private Map<String, Number> minValues = new HashMap<>();
    private Map<String, Number> maxValues = new HashMap<>();
    private Set<String> nonNumberProperties = new HashSet<>();

    private String getKey(Class<?> modelClass, String propertyName) {
        return modelClass.getName() + Strings.DOT + propertyName;
    }

    public void putMinValue(Class<?> modelClass, String propertyName, Number minValue) {
        String key = getKey(modelClass, propertyName);
        if (minValue != null) {
            this.minValues.put(key, minValue);
        } else {
            this.nonNumberProperties.add(key);
        }
    }

    public Number getMinValue(Class<?> modelClass, String propertyName) {
        String key = getKey(modelClass, propertyName);
        return this.minValues.get(key);
    }

    public void putMaxValue(Class<?> modelClass, String propertyName, Number maxValue) {
        String key = getKey(modelClass, propertyName);
        if (maxValue != null) {
            this.maxValues.put(key, maxValue);
        } else {
            this.nonNumberProperties.add(key);
        }
    }

    public Number getMaxValue(Class<?> modelClass, String propertyName) {
        String key = getKey(modelClass, propertyName);
        return this.maxValues.get(key);
    }

    public boolean isNonNumber(Class<?> modelClass, String propertyName) {
        String key = getKey(modelClass, propertyName);
        return this.nonNumberProperties.contains(key);
    }

}
