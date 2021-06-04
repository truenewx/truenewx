package org.truenewx.tnxjee.model.validation.constraint.validator;

import org.truenewx.tnxjee.model.validation.constraint.NotContains;
import org.truenewx.tnxjee.model.validation.constraint.NotContainsSqlChars;

/**
 * 不能包含SQL字符约束校验器
 *
 * @author jianglei
 */
public class NotContainsSqlCharsValidator
        extends AbstractNotContainsValidator<NotContainsSqlChars> {

    @Override
    public void initialize(NotContainsSqlChars annotation) {
        super.initialize(annotation.annotationType().getAnnotation(NotContains.class));
    }

}
