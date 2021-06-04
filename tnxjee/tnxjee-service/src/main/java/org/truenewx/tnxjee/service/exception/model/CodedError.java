package org.truenewx.tnxjee.service.exception.model;

/**
 * 具有编码的错误
 *
 * @author jianglei
 */
public class CodedError {

    private String code;
    private String message;

    public CodedError() {
    }

    public CodedError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
