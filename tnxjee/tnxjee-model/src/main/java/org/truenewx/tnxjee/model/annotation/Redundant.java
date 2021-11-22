package org.truenewx.tnxjee.model.annotation;

import java.lang.annotation.*;

import org.truenewx.tnxjee.core.Strings;

/**
 * 标注属性冗余
 *
 * @author jianglei
 */
@Documented
@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Redundant {

    /**
     * @return 冗余来源说明
     */
    String value() default Strings.EMPTY;

}
