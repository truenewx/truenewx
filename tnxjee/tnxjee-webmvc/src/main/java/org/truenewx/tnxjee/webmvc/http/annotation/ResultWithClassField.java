package org.truenewx.tnxjee.webmvc.http.annotation;

import java.lang.annotation.*;

/**
 * 标注返回结果在内部RPC调用时附带类型字段
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResultWithClassField {
}
