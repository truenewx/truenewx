package org.truenewx.tnxjee.core.caption;

import java.lang.annotation.*;

/**
 * 说明注解集
 *
 * @author jianglei
 */
@Documented
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Captions {

    Caption[] value() default {};

}
