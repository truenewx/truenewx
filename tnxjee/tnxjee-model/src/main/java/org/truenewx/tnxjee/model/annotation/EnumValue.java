package org.truenewx.tnxjee.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 枚举值
 * 
 * @author jianglei
 * 
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValue {
    /**
     * 值
     * 
     * @return 值
     */
    String value();
}
