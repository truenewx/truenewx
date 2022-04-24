package org.truenewx.tnxjee.model.validation.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.model.validation.constraint.validator.NotContainsIllegalFilenameCharsValidator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 不能包含非法文件名字符，这些特殊字符会导致标注的字段无法作为文件系统中文件名的一部分
 *
 * @author jianglei
 */
@Target(FIELD)
@Retention(RUNTIME)
@Documented
@Inherited
@NotContains({ Strings.SLASH, Strings.BACKSLASH, Strings.COLON, Strings.ASTERISK, Strings.QUESTION,
        Strings.DOUBLE_QUOTES, Strings.LESS_THAN, Strings.GREATER_THAN, Strings.VERTICAL_BAR })
@ReportAsSingleViolation
@Constraint(validatedBy = NotContainsIllegalFilenameCharsValidator.class)
public @interface NotContainsIllegalFilenameChars {

    String message() default NotContains.DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
