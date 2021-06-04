package org.truenewx.tnxjee.model.annotation;

import java.lang.annotation.*;

/**
 * 标注属性在Get请求传递参数时忽略
 */
@Documented
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RequestParamIgnore {
}
