package org.truenewx.tnxjeex.captcha.web;

import javax.servlet.http.HttpSession;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjeex.captcha.core.AbstractCaptchaVerifier;

/**
 * 基于HTTP会话的验证码需求判定器
 */
@Component
public class HttpSessionCaptchaVerifier extends AbstractCaptchaVerifier {

    private static final String NAME_PREFIX = "Captcha.";

    /**
     * 场景阈值，每个场景的使用次数达到该阈值时要求必须经过行为验证码校验
     */
    private int threshold;

    public HttpSessionCaptchaVerifier(Environment environment) {
        String threshold = environment.getProperty("tnxjeex.captcha.requirement.threshold");
        this.threshold = MathUtil.parseInt(threshold, 1);
    }

    private String getName(String scene) {
        return NAME_PREFIX + scene;
    }

    @Override
    public void increase(String scene) {
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
    public void clear(String scene) {
        SpringWebContext.removeFromSession(getName(scene));
    }

    @Override
    public boolean isRequired(String scene) {
        Integer num = SpringWebContext.getFromSession(getName(scene));
        return num != null && num >= this.threshold;
    }

}
