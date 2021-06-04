package org.truenewx.tnxjee.core.util.function;

import java.util.function.Supplier;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.config.ProfileProperties;

/**
 * 供应者：获取当前profile
 *
 * @author jianglei
 */
@Component
public class ProfileSupplier implements Supplier<String>, ApplicationContextAware {

    private String profile = Strings.EMPTY; // 默认为空，表示无profile区分
    @Autowired
    private ProfileProperties profileProperties;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Environment env = context.getEnvironment();
        String[] profiles = env.getActiveProfiles();
        if (profiles.length > 0) {
            this.profile = profiles[0];
        }
    }

    @Override
    public String get() {
        return this.profile;
    }

    public boolean isFormal() {
        return this.profileProperties.isFormal();
    }
}
