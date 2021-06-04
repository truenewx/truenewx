package org.truenewx.tnxjee.model.validation.constraint;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.truenewx.tnxjee.model.validation.constraint.validator.TagLimitValidator;

/**
 * 标签限定
 *
 * @author jianglei
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TagLimitValidator.class)
public @interface TagLimit {

    String DEFAULT_MESSAGE = "{org.truenewx.tnxjee.model.validation.constraint.TagLimit.message}";

    /**
     * 允许的标签名称清单
     *
     * @return 允许的标签名称清单
     */
    String[] allowed() default {};

    /**
     * 禁止的标签名称清单
     *
     * @return 禁止的标签名称清单
     */
    String[] forbidden() default {};

    String message() default DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
