package org.truenewx.tnxjee.repo.validation.rule.builder;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.model.validation.constraint.NotContainsAngleBracket;
import org.truenewx.tnxjee.model.validation.constraint.NotContainsHtmlChars;
import org.truenewx.tnxjee.model.validation.constraint.NotContainsIllegalFilenameChars;
import org.truenewx.tnxjee.model.validation.rule.MarkRule;

/**
 * 标识规则的构建器
 *
 * @author jianglei
 */
@Component
public class MarkRuleBuilder implements ValidationRuleBuilder<MarkRule> {

    @Override
    public Class<?>[] getConstraintTypes() {
        return new Class<?>[]{ NotNull.class, NotEmpty.class, NotBlank.class, Email.class,
                NotContainsAngleBracket.class, NotContainsHtmlChars.class, NotContainsIllegalFilenameChars.class };
    }

    @Override
    public void update(Annotation annotation, MarkRule rule) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        Collection<Class<? extends Annotation>> annotationTypes = rule.getAnnotationTypes();
        if (annotationType == NotBlank.class) {
            // 覆盖不能为空和不能为null
            annotationTypes.remove(NotEmpty.class);
            annotationTypes.remove(NotNull.class);
        } else if (annotationType == NotEmpty.class) {
            // 已经有不能为空白，则忽略
            if (annotationTypes.contains(NotBlank.class)) {
                return;
            }
            // 覆盖不能为null
            annotationTypes.remove(NotNull.class);
        } else if (annotationType == NotNull.class) {
            // 已经有不能为空白或不能为空，则忽略
            if (annotationTypes.contains(NotBlank.class) || annotationTypes.contains(NotEmpty.class)) {
                return;
            }
        }
        annotationTypes.add(annotationType);
    }

    @Override
    public MarkRule create(Annotation annotation) {
        return new MarkRule(annotation.annotationType());
    }

}
