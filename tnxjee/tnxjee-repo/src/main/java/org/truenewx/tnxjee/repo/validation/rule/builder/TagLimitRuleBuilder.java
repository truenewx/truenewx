package org.truenewx.tnxjee.repo.validation.rule.builder;

import java.lang.annotation.Annotation;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.model.validation.constraint.TagLimit;
import org.truenewx.tnxjee.model.validation.rule.TagLimitRule;

/**
 * 标签限定规则构建器
 *
 * @author jianglei
 */
@Component
public class TagLimitRuleBuilder implements ValidationRuleBuilder<TagLimitRule> {

    @Override
    public Class<?>[] getConstraintTypes() {
        return new Class<?>[]{ TagLimit.class };
    }

    @Override
    public void update(Annotation annotation, TagLimitRule rule) {
        if (annotation instanceof TagLimit) {
            TagLimit tagLimit = (TagLimit) annotation;
            rule.addAllowed(tagLimit.allowed());
            rule.addForbidden(tagLimit.forbidden());
        }
    }

    @Override
    public TagLimitRule create(Annotation annotation) {
        TagLimitRule rule = new TagLimitRule();
        update(annotation, rule);
        return rule;
    }

}
