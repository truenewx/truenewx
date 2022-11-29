package org.truenewx.tnxjee.webmvc.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.truenewx.tnxjee.core.config.AppConstants;

/**
 * JWT对称加密方
 *
 * @author jianglei
 */
public abstract class JwtSymmetricEncryption extends JwtSymmetricSecretKeySupport implements JwtEncryption {

    @Value(AppConstants.EL_SPRING_APP_NAME)
    private String name;

    @Override
    public final String getEncryptionName() {
        return this.name;
    }

    @Override
    public String getPayload(String type) {
        return null;
    }

    @Override
    public final boolean isSymmetric(String type) {
        return true;
    }

    @Override
    public final String getEncryptSecretKey(String type) {
        return getSecretKey(type);
    }

}
