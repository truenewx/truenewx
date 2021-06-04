package org.truenewx.tnxjee.model.validation.config;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.truenewx.tnxjee.model.Model;
import org.truenewx.tnxjee.model.validation.rule.ValidationRule;

/**
 * 校验设置
 *
 * @author jianglei
 */
public class ValidationConfiguration {
    /**
     * 模型类
     */
    private Class<? extends Model> modelClass;
    /**
     * 属性名-约束集的映射
     */
    private Map<String, Set<ValidationRule>> ruleMapping;

    public ValidationConfiguration(Class<? extends Model> modelClass) {
        this.modelClass = modelClass;
        this.ruleMapping = new HashMap<>();
    }

    public Class<? extends Model> getModelClass() {
        return this.modelClass;
    }

    public synchronized void addRule(String propertyName, ValidationRule rule) {
        if (rule != null) {
            getRules(propertyName).add(rule);
        }
    }

    public synchronized Set<ValidationRule> getRules(String propertyName) {
        Set<ValidationRule> rules = this.ruleMapping.get(propertyName);
        if (rules == null) {
            rules = new LinkedHashSet<>(); // 保持规则加入的顺序
            this.ruleMapping.put(propertyName, rules);
        }
        return rules;
    }

    @SuppressWarnings("unchecked")
    public synchronized <R extends ValidationRule> R getRule(String propertyName,
            Class<R> ruleClass) {
        Set<ValidationRule> rules = getRules(propertyName);
        for (ValidationRule rule : rules) {
            if (rule.getClass() == ruleClass) {
                return (R) rule;
            }
        }
        return null;
    }

    public Set<String> getPropertyNames() {
        return this.ruleMapping.keySet();
    }

    public void copy(String sourcePropertyName, String targetPropertyName) {
        Set<ValidationRule> rules = this.ruleMapping.get(sourcePropertyName);
        if (rules != null) {
            this.ruleMapping.put(targetPropertyName, rules);
        } else {
            this.ruleMapping.remove(targetPropertyName);
        }
    }
}
