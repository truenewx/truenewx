package org.truenewx.tnxjee.webmvc.jackson;

import java.lang.annotation.*;

/**
 * 标注方法结果或结果属性在内部RPC调用序列化为JSON串时附带类型字段，一般用于类型为{@link Iterable}或{@link java.util.Map}，且元素类型不是简单类型时<br/>
 * 简单类型判断：{@link org.springframework.beans.BeanUtils#isSimpleValueType(Class)}
 */
@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonWithClassProperty {
}
