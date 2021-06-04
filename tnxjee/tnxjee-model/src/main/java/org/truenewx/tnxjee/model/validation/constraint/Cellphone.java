package org.truenewx.tnxjee.model.validation.constraint;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.model.validation.constraint.validator.CellphoneValidator;

/**
 * 手机号码约束
 *
 * @author jianglei
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = StringUtil.CELLPHONE_PATTERN)
@Constraint(validatedBy = CellphoneValidator.class)
public @interface Cellphone {

    String DEFAULT_MESSAGE = "{org.truenewx.tnxjee.model.validation.constraint.Cellphone.message}";

    String message() default DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
