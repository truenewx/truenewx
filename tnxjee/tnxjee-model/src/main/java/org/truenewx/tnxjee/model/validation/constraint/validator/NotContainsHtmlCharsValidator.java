package org.truenewx.tnxjee.model.validation.constraint.validator;

import org.truenewx.tnxjee.model.validation.constraint.NotContains;
import org.truenewx.tnxjee.model.validation.constraint.NotContainsHtmlChars;

/**
 * 不能包含HTML字符约束校验器
 *
 * @author jianglei
 */
public class NotContainsHtmlCharsValidator
        extends AbstractNotContainsValidator<NotContainsHtmlChars> {

    @Override
    public void initialize(NotContainsHtmlChars annotation) {
        super.initialize(annotation.annotationType().getAnnotation(NotContains.class));
    }

}
