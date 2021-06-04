package org.truenewx.tnxjee.model.validation.constraint;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import org.truenewx.tnxjee.model.validation.constraint.validator.NotContainsSpecialCharsValidator;

/**
 * 不能包含特殊字符，这些特殊字符可能破坏页面结构或影响数据查询的准确性
 *
 * @author jianglei
 */
@Documented
@Target(FIELD)
@Retention(RUNTIME)
@ReportAsSingleViolation
@Constraint(validatedBy = NotContainsSpecialCharsValidator.class)
public @interface NotContainsSpecialChars {

    /**
     * 逗号有时会作为存储分隔符，成为不能包含的特殊字符
     *
     * @return 能否包含逗号，默认为true
     */
    boolean comma() default true;

    /**
     *
     * @return 能否包含HTML关键字符，默认为false
     */
    boolean html() default false;

    /**
     *
     * @return 能否包含SQL关键字符，默认为true
     */
    boolean sql() default true;

    String message() default NotContains.DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
