package org.truenewx.tnxjee.model.validation.constraint.validator;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;

import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.StringUtil;

/**
 * 抽象的正则表达式校验器
 *
 * @param <A> 字段约束注解类型
 */
public abstract class AbstractPatternValidator<A extends Annotation> implements ConstraintValidator<A, CharSequence> {

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Pattern pattern = getConstraintAnnotationType().getAnnotation(Pattern.class);
        return StringUtil.regexMatch(value.toString(), pattern.regexp());
    }

    protected Class<? extends Annotation> getConstraintAnnotationType() {
        return ClassUtil.getActualGenericType(getClass(), 0);
    }

}
