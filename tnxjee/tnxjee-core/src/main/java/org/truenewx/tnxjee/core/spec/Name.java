package org.truenewx.tnxjee.core.spec;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 名称
 *
 * @author jianglei
 * 
 */
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {
    /**
     * 名称
     *
     * @return 名称
     */
    String value() default "";
}
