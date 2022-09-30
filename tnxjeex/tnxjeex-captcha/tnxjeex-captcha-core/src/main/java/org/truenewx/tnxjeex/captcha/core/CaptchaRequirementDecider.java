package org.truenewx.tnxjeex.captcha.core;

import org.truenewx.tnxjee.core.Strings;

/**
 * 验证码需求判定器
 *
 * @author jianglei
 */
public interface CaptchaRequirementDecider {

    default String getScene(Enum<?> enumConstant) {
        return enumConstant.getClass().getSimpleName() + Strings.DOT + enumConstant.name();
    }

    void increaseErrorNum(String scene);

    void clearErrorNum(String scene);

    boolean isRequired(String scene);

}
