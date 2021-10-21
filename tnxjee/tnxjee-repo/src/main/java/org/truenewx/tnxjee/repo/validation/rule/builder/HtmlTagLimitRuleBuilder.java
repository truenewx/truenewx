package org.truenewx.tnxjee.repo.validation.rule.builder;

import java.lang.annotation.Annotation;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.model.validation.constraint.HtmlTagLimit;
import org.truenewx.tnxjee.model.validation.rule.HtmlTagLimitRule;

/**
 * Html标签限定规则构建器
 *
 * @author jianglei
 */
@Component
public class HtmlTagLimitRuleBuilder implements ValidationRuleBuilder<HtmlTagLimitRule> {

    @Override
    public Class<?>[] getConstraintTypes() {
        return new Class<?>[]{ HtmlTagLimit.class };
    }

    @Override
    public void update(Annotation annotation, HtmlTagLimitRule rule) {
        if (annotation instanceof HtmlTagLimit) {
            HtmlTagLimit htmlTagLimit = (HtmlTagLimit) annotation;
            rule.addAllowed(htmlTagLimit.allowed());
            rule.addForbidden(htmlTagLimit.forbidden());
        }
    }

    @Override
    public HtmlTagLimitRule create(Annotation annotation) {
        HtmlTagLimitRule rule = new HtmlTagLimitRule();
        update(annotation, rule);
        return rule;
    }

}
