package org.truenewx.tnxjeex.payment.core.gateway;

import org.truenewx.tnxjee.service.exception.BusinessException;

/**
 * 具有响应内容的业务异常
 *
 * @author jianglei
 */
public class RespondBusinessException extends BusinessException {

    private static final long serialVersionUID = 8762277251494217080L;

    private String response;

    public RespondBusinessException(String response, String code, Object... args) {
        super(code, args);
        this.response = response;
    }

    public String getResponse() {
        return this.response;
    }

}
