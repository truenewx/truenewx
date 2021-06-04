package org.truenewx.tnxjee.service.exception;

/**
 * 没有访问权限的异常
 */
public class NoAccessAuthority extends BusinessException {

    private static final long serialVersionUID = -3432282540107390181L;

    public NoAccessAuthority() {
        super("error.service.security.no_access_authority");
    }

}
