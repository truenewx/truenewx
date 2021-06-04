package org.truenewx.tnxjee.model.validation.constraint;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import org.truenewx.tnxjee.model.validation.constraint.validator.NotContainsAngleBracketValidator;

/**
 * 不能包含尖括弧
 *
 * @author jianglei
 */
@Target(FIELD)
@Retention(RUNTIME)
@Documented
@NotContains({ "<", ">" })
@ReportAsSingleViolation
@Constraint(validatedBy = NotContainsAngleBracketValidator.class)
public @interface NotContainsAngleBracket {

    String message() default NotContains.DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
