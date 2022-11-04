package org.truenewx.tnxjee.webmvc.jwt;

/**
 * JWT解析器
 *
 * @author jianglei
 */
public interface JwtParser {

    boolean isAvailable();

    <T> T parse(String type, String jwt, Class<T> expectedType);

}
