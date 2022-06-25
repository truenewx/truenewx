package org.truenewx.tnxjee.model.annotation;

import java.lang.annotation.*;

/**
 * 标注类型为被索引类型
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Indexed {

    /**
     * @return 索引存储位置，相对于索引存储根目录的相对路径
     */
    String value();

}
