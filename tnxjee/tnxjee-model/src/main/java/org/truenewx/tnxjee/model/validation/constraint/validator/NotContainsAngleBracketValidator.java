package org.truenewx.tnxjee.model.validation.constraint.validator;

import org.truenewx.tnxjee.model.validation.constraint.NotContains;
import org.truenewx.tnxjee.model.validation.constraint.NotContainsAngleBracket;

/**
 * 不能包含尖括弧约束校验器
 *
 * @author jianglei
 */
public class NotContainsAngleBracketValidator
        extends AbstractNotContainsValidator<NotContainsAngleBracket> {

    @Override
    public void initialize(NotContainsAngleBracket annotation) {
        super.initialize(annotation.annotationType().getAnnotation(NotContains.class));
    }

}
