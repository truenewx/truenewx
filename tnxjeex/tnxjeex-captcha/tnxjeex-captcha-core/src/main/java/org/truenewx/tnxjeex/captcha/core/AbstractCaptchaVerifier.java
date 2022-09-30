package org.truenewx.tnxjeex.captcha.core;

import org.springframework.beans.factory.annotation.Autowired;

import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;

public abstract class AbstractCaptchaVerifier implements CaptchaVerifier {

    @Autowired
    private CaptchaService captchaService;

    @Override
    public boolean verify(String scene, String verification) {
        if (verification != null) {
            CaptchaVO vo = new CaptchaVO();
            vo.setCaptchaVerification(verification);
            return this.captchaService.verification(vo).isSuccess();
        } else {
            return !isRequired(scene);
        }
    }

}
