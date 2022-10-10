package org.truenewx.tnxjeex.captcha.core;

import java.util.Properties;

import org.springframework.beans.factory.DisposableBean;

import com.anji.captcha.model.common.Const;
import com.anji.captcha.service.CaptchaCacheService;
import com.anji.captcha.service.CaptchaService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;
import com.anji.captcha.service.impl.DefaultCaptchaServiceImpl;

/**
 * 可销毁的默认校验码服务
 */
public class DisposableDefaultCaptchaService extends DefaultCaptchaServiceImpl implements DisposableBean {

    public static final String TYPE = "disposable-default";

    @Override
    public String captchaType() {
        return TYPE;
    }

    @Override
    public void init(Properties config) {
        cacheType = config.getProperty(Const.CAPTCHA_CACHETYPE, "local");
        for (CaptchaService captchaService : CaptchaServiceFactory.instances.values()) {
            String captchaType = captchaService.captchaType();
            if (!TYPE.equals(captchaType) && !super.captchaType().equals(captchaType)) {
                captchaService.init(config);
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        CaptchaCacheService cacheService = getCacheService(cacheType);
        if (cacheService instanceof DisposableBean) {
            ((DisposableBean) cacheService).destroy();
        }
    }

}
