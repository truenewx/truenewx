package org.truenewx.tnxjee.repo.jpa.codegen;

/**
 * JPA枚举转换器生成器
 *
 * @author jianglei
 */
public interface JpaEnumConverterGenerator {

    String generate(Class<?> enumClass) throws Exception;

}
