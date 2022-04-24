package org.truenewx.tnxjee.model.validation.constraint.validator;

import org.truenewx.tnxjee.model.validation.constraint.NotContains;
import org.truenewx.tnxjee.model.validation.constraint.NotContainsIllegalFilenameChars;

/**
 * 不能包含非法文件名字符约束校验器
 *
 * @author jianglei
 */
public class NotContainsIllegalFilenameCharsValidator
        extends AbstractNotContainsValidator<NotContainsIllegalFilenameChars> {

    @Override
    public void initialize(NotContainsIllegalFilenameChars annotation) {
        super.initialize(annotation.annotationType().getAnnotation(NotContains.class));
    }

}
