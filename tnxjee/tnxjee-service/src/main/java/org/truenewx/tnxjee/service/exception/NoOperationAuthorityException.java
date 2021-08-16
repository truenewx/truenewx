package org.truenewx.tnxjee.service.exception;

import org.apache.commons.lang3.ArrayUtils;

/**
 * 没有操作权限的异常
 */
public class NoOperationAuthorityException extends BusinessException {

    private static final long serialVersionUID = -3432282540107390181L;

    public NoOperationAuthorityException(Object... args) {
        super(getCode(args), args);
    }

    private static String getCode(Object[] args) {
        return ArrayUtils.isEmpty(args) ? "error.service.security.no_this_operation_authority"
                : "error.service.security.no_specified_operation_authority";
    }

}
