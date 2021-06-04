package org.truenewx.tnxjee.model.validation.annotation;

import java.lang.annotation.*;

/**
 * 引用约束
 *
 * @author jianglei
 */
@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ReferenceConstraint {

    /**
     * @return 引用模型包含的属性名称集合，为空则不包含任何属性
     */
    String[] value() default {};

}
