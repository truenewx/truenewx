package org.truenewx.tnxjee.model.validation.constraint;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.model.validation.constraint.validator.RegionCodeValidator;

/**
 * 标注属性是行政区划代码
 *
 * @author jianglei
 */
@Documented
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = StringUtil.REGION_CODE_PATTERN)
@Constraint(validatedBy = RegionCodeValidator.class)
public @interface RegionCode {

    String DEFAULT_MESSAGE = "{org.truenewx.tnxjee.model.validation.constraint.RegionCode.message}";

    String message() default DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return 显示名称的开始级别，默认为1，即从省级区域开始
     */
    int captionBeginLevel() default 1;

    /**
     * @return 显示名称中的省市是否包含后缀，默认为false
     */
    boolean withSuffix() default false;

}
