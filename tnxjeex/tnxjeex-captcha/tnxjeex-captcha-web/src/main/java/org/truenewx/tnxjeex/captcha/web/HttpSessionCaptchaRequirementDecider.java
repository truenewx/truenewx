package org.truenewx.tnxjeex.captcha.web;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjeex.captcha.core.CaptchaRequirementDecider;

/**
 * 基于HTTP会话的验证码需求判定器
 */
@Component
public class HttpSessionCaptchaRequirementDecider implements CaptchaRequirementDecider {

    private static final String NAME_PREFIX = "Captcha.";

    private String getName(String scene) {
        return NAME_PREFIX + scene;
    }

    @Override
    public void increaseErrorNum(String scene) {
        HttpSession session = SpringWebContext.getSession();
        String name = getName(scene);
        Integer num = (Integer) session.getAttribute(name);
        if (num == null) {
            num = 1;
        } else {
            num++;
        }
        session.setAttribute(name, num);
    }

    @Override
    public void clearErrorNum(String scene) {
        SpringWebContext.removeFromSession(getName(scene));
    }

    @Override
    public boolean isRequired(String scene) {
        Integer num = SpringWebContext.getFromSession(getName(scene));
        return num != null && num > 0;
    }

}
