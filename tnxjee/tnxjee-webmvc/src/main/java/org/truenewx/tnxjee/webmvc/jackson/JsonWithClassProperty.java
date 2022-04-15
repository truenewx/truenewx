package org.truenewx.tnxjee.webmvc.jackson;

import java.lang.annotation.*;

/**
 * 标注方法结果或结果属性在内部RPC调用时附带类型字段，一般用于类型为{@link Iterable}或{@link java.util.Map}，且元素类型不是集合和简单类型时。
 * 因为在序列化之前构建序列化器时，集合中的元素类型只能识别为Object，无法判断是否需要附带类型字段，不该附带时附带将导致集合类型被附带而导致反序列化错误。<br/>
 * 简单类型判断：{@link org.springframework.beans.BeanUtils#isSimpleValueType(Class)}
 */
@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonWithClassProperty {
}
