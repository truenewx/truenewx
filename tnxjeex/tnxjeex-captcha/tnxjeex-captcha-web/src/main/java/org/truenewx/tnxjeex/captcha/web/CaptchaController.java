package org.truenewx.tnxjeex.captcha.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @PostMapping("/generate")
    @ConfigAnonymous
    public ResponseModel generate(@RequestBody CaptchaVO vo) {
        return this.captchaService.get(vo);
    }

    @PostMapping("/check")
    @ConfigAnonymous
    public ResponseModel check(@RequestBody CaptchaVO vo) {
        return this.captchaService.check(vo);
    }

}
