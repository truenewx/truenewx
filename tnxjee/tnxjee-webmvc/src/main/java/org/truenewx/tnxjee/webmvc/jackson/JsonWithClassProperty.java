package org.truenewx.tnxjee.webmvc.jackson;

import java.lang.annotation.*;

/**
 * 标注方法结果或结果属性在内部RPC调用时附带类型字段
 */
@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonWithClassProperty {
}
