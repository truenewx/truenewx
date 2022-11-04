package org.truenewx.tnxjee.webmvc.jwt;

/**
 * JWT生成器
 *
 * @author jianglei
 */
public interface JwtGenerator {

    boolean isAvailable();

    String generate(String type, Object source);

}
