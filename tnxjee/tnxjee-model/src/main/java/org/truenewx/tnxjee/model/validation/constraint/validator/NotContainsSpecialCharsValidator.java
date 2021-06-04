package org.truenewx.tnxjee.model.validation.constraint.validator;

import java.util.HashSet;
import java.util.Set;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.model.validation.constraint.NotContains;
import org.truenewx.tnxjee.model.validation.constraint.NotContainsHtmlChars;
import org.truenewx.tnxjee.model.validation.constraint.NotContainsSpecialChars;
import org.truenewx.tnxjee.model.validation.constraint.NotContainsSqlChars;

/**
 * 不能包含特殊字符约束校验器
 *
 * @author jianglei
 */
public class NotContainsSpecialCharsValidator
        extends AbstractNotContainsValidator<NotContainsSpecialChars> {

    @Override
    public void initialize(NotContainsSpecialChars annotation) {
        Set<String> values = new HashSet<>();
        if (!annotation.comma()) {
            values.add(Strings.COMMA);
        }
        if (!annotation.html()) {
            CollectionUtil.addAll(values,
                    NotContainsHtmlChars.class.getAnnotation(NotContains.class).value());
        }
        if (!annotation.sql()) {
            CollectionUtil.addAll(values,
                    NotContainsSqlChars.class.getAnnotation(NotContains.class).value());
        }
        setValues(values.toArray(new String[values.size()]));
    }

}
