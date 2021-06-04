package org.truenewx.tnxjeex.notice.service.sms.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.util.Profiles;
import org.truenewx.tnxjee.core.util.function.ProfileSupplier;

/**
 * 抽象的短信内容发送器
 */
public abstract class AbstractSmsContentSender implements SmsContentSender {

    private String[] types;
    @Autowired
    private ProfileSupplier profileSupplier;

    @Override
    public String[] getTypes() {
        return this.types;
    }

    public void setTypes(String... types) {
        this.types = types;
    }

    @Override
    public int getIntervalSeconds() {
        String profile = this.profileSupplier.get();
        switch (profile) {
            case Profiles.JUNIT:
            case Profiles.LOCAL:
            case Profiles.DEV:
                return 10;
            case Profiles.TEST:
                return 30;
        }
        return 60;
    }

}
