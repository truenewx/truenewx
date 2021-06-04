package org.truenewx.tnxjee.model.validation.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.truenewx.tnxjee.model.validation.constraint.validator.NotContainsValidator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 不能包含字符串约束<br/>
 * 注意：不支持限制空格
 *
 * @author jianglei
 */
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Documented
@Inherited
@Constraint(validatedBy = NotContainsValidator.class)
public @interface NotContains {

    String DEFAULT_MESSAGE = "{org.truenewx.tnxjee.model.validation.constraint.NotContains.message}";

    /**
     * @return 不能包含的字符串集，其中如有空格会被忽略
     */
    String[] value();

    String message() default DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
