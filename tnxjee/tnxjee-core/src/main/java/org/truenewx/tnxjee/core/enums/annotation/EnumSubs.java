package org.truenewx.tnxjee.core.enums.annotation;

import java.lang.annotation.*;

/**
 * 枚举子集集合
 *
 * @author jianglei
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumSubs {

    EnumSub[] value() default {};

}
