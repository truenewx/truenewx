package org.truenewx.tnxjee.model.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 组件类型
 *
 * @author jianglei
 * 
 */
@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentType {
    /**
     * 当属性或参数类型为集合或Map时有效，Map时指定值类型
     *
     * @return 集合中元素的类型
     */
    Class<?> value() default Object.class;
}
