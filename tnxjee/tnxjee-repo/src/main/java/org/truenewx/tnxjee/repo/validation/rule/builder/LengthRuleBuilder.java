package org.truenewx.tnxjee.repo.validation.rule.builder;

import java.lang.annotation.Annotation;

import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.model.validation.rule.LengthRule;

/**
 * 字符串长度规则的构建器
 *
 * @author jianglei
 */
@Component
public class LengthRuleBuilder implements ValidationRuleBuilder<LengthRule> {

    @Override
    public Class<?>[] getConstraintTypes() {
        return new Class<?>[]{ Length.class };
    }

    @Override
    public void update(Annotation annotation, LengthRule rule) {
        if (annotation.annotationType() == Length.class) {
            Length length = (Length) annotation;
            int min = length.min();
            if (min > rule.getMin()) {
                rule.setMin(min);
            }
            int max = length.max();
            if (max < rule.getMax()) {
                rule.setMax(max);
            }
        }
    }

    @Override
    public LengthRule create(Annotation annotation) {
        if (annotation.annotationType() == Length.class) {
            Length length = (Length) annotation;
            return new LengthRule(length.min(), length.max());
        }
        return null;
    }

}
