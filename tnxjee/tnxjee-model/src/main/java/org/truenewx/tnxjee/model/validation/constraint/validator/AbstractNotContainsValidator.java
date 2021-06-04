package org.truenewx.tnxjee.model.validation.constraint.validator;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.model.validation.constraint.NotContains;

/**
 * 抽象的不能包含约束校验器
 *
 * @author jianglei
 */
public abstract class AbstractNotContainsValidator<A extends Annotation>
        implements ConstraintValidator<A, Object> {

    private String[] values;

    protected void setValues(String[] values) {
        this.values = values;
    }

    public void initialize(NotContains annotation) {
        setValues(annotation.value());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof CharSequence) {
            String s = value.toString();
            if (StringUtils.isNotEmpty(s)) {
                for (String v : this.values) {
                    if (s.contains(v)) {
                        return false;
                    }
                }
            }
        } else if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            for (Object obj : collection) {
                if (!isValid(obj, context)) {
                    return false;
                }
            }
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            for (Object obj : array) {
                if (!isValid(obj, context)) {
                    return false;
                }
            }
        }
        return true;
    }

}
