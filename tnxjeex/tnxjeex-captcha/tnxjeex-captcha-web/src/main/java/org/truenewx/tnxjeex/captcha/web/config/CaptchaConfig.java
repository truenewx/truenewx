package org.truenewx.tnxjeex.captcha.web.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjeex.captcha.core.DisposableDefaultCaptchaService;
import org.truenewx.tnxjeex.captcha.core.RamCaptchaCacheService;

import com.anji.captcha.model.common.Const;
import com.anji.captcha.service.CaptchaService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;

@Configuration
public class CaptchaConfig {

    @Bean
    public CaptchaService captchaService() {
        Properties config = new Properties();
        config.setProperty(Const.CAPTCHA_TYPE, DisposableDefaultCaptchaService.TYPE);
        config.setProperty(Const.CAPTCHA_CACHETYPE, RamCaptchaCacheService.TYPE);
        config.setProperty(Const.CAPTCHA_WATER_MARK, Strings.EMPTY);
        config.setProperty(Const.CAPTCHA_WATER_FONT, "SimSun"); // 宋体
        config.setProperty(Const.CAPTCHA_FONT_TYPE, "WenQuanZhengHei.ttf");
        return CaptchaServiceFactory.getInstance(config);
    }

}
