package org.truenewx.tnxjee.test.service.support;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.service.security.access.GrantedAuthorityDecider;

/**
 * 模拟的获权判定器
 */
@Component
public class MockGrantedAuthorityDecider implements GrantedAuthorityDecider {

    @Override
    public boolean isGranted(Collection<? extends GrantedAuthority> authorities, String type, String rank, String app,
            String permission) {
        return true;
    }

}
