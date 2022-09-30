package org.truenewx.tnxjeex.captcha.core;

import org.truenewx.tnxjee.core.Strings;

/**
 * 验证码校验器
 *
 * @author jianglei
 */
public interface CaptchaVerifier {

    default String getScene(Enum<?> enumConstant) {
        return enumConstant.getClass().getSimpleName() + Strings.DOT + enumConstant.name();
    }

    void increase(String scene);

    void clear(String scene);

    boolean isRequired(String scene);

    boolean verify(String scene, String verification);

    default boolean verify(Enum<?> sceneEnum, String verification) {
        return verify(getScene(sceneEnum), verification);
    }

}
