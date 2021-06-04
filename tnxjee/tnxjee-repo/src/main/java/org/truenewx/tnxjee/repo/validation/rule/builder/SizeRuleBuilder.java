package org.truenewx.tnxjee.repo.validation.rule.builder;

import java.lang.annotation.Annotation;

import javax.validation.constraints.Size;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.model.validation.rule.SizeRule;

/**
 * 字符串长度和集合大小规则的构建器
 *
 * @author jianglei
 */
@Component
public class SizeRuleBuilder implements ValidationRuleBuilder<SizeRule> {

    @Override
    public Class<?>[] getConstraintTypes() {
        return new Class<?>[]{ Size.class };
    }

    @Override
    public void update(Annotation annotation, SizeRule rule) {
        if (annotation.annotationType() == Size.class) {
            Size size = (Size) annotation;
            int min = size.min();
            if (min > rule.getMin()) {
                rule.setMin(min);
            }
            int max = size.max();
            if (max < rule.getMax()) {
                rule.setMax(max);
            }
        }
    }

    @Override
    public SizeRule create(Annotation annotation) {
        if (annotation.annotationType() == Size.class) {
            Size size = (Size) annotation;
            return new SizeRule(size.min(), size.max());
        }
        return null;
    }

}
