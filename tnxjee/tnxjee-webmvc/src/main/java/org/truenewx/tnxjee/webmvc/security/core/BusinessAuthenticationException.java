package org.truenewx.tnxjee.webmvc.security.core;

import org.springframework.security.core.AuthenticationException;
import org.truenewx.tnxjee.service.exception.BusinessException;

/**
 * 业务鉴权异常
 */
public class BusinessAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = -7826129157270679401L;

    public BusinessAuthenticationException(BusinessException cause) {
        super(cause.getCode(), cause);
    }

    public BusinessAuthenticationException(String code, Object... args) {
        this(new BusinessException(code, args));
    }

}
