package org.truenewx.tnxjeex.captcha.web.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.core.Strings;

import com.anji.captcha.model.common.Const;
import com.anji.captcha.service.CaptchaService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;

@Configuration
public class CaptchaConfig {

    @Bean
    public CaptchaService captchaService() {
        Properties config = new Properties();
        config.setProperty(Const.CAPTCHA_WATER_MARK, Strings.EMPTY);
        config.setProperty(Const.CAPTCHA_FONT_TYPE, "WenQuanZhengHei.ttf");
        return CaptchaServiceFactory.getInstance(config);
    }

}
