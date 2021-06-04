package org.truenewx.tnxjee.webmvc.exception.model;

import java.util.Collection;

import org.truenewx.tnxjee.service.exception.model.ExceptionError;

/**
 * 异常错误消息体
 *
 * @author jianglei
 */
public class ExceptionErrorBody {

    private ExceptionError[] errors;

    public ExceptionErrorBody() {
    }

    public ExceptionErrorBody(Collection<ExceptionError> errors) {
        this.errors = errors.toArray(new ExceptionError[0]);
    }

    public ExceptionError[] getErrors() {
        return this.errors;
    }

    public void setErrors(ExceptionError[] errors) {
        this.errors = errors;
    }

}
