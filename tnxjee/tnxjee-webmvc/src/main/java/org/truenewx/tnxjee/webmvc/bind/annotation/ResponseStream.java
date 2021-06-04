package org.truenewx.tnxjee.webmvc.bind.annotation;

import java.lang.annotation.*;

/**
 * 标注一个控制器方法以二进制流的方式响应结果，以便于处理过程中的异常处理
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseStream {
}
