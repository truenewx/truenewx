package org.truenewx.tnxjee.webmvc.http.annotation;

import java.lang.annotation.*;

/**
 * 标注返回结果在内部RPC调用时附带类型字段，一般用于返回结果类型为{@link Iterable}或{@link java.util.Map}，且元素类型不是简单类型时<br/>
 * 简单类型判断：{@link org.springframework.beans.BeanUtils#isSimpleValueType(Class)}
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResultWithClassField {
}
