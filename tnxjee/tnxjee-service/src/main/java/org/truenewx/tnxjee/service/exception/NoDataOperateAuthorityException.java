package org.truenewx.tnxjee.service.exception;

import org.apache.commons.lang3.ArrayUtils;

/**
 * 没有数据权限的异常
 */
public class NoDataOperateAuthorityException extends BusinessException {

    private static final long serialVersionUID = -421574090623208780L;

    public NoDataOperateAuthorityException(Object... args) {
        super(getCode(args), args);
    }

    private static String getCode(Object[] args) {
        return ArrayUtils.isEmpty(args) ? "error.service.security.no_this_data_operate_authority"
                : "error.service.security.no_specified_data_operate_authority";
    }

}
