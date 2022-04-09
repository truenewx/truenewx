package org.truenewx.tnxjee.webmvc.jackson;

import java.lang.annotation.*;

/**
 * 标注方法结果或结果属性在内部RPC调用序列化为JSON串时附带类型字段
 *
 * @author jianglei
 */
@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonWithClassProperty {
}
