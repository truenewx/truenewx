package org.truenewx.tnxjee.webmvc.view.tagext;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.service.exception.model.ExceptionError;
import org.truenewx.tnxjee.webmvc.exception.message.ResolvableExceptionMessageSaver;

/**
 * 错误标签支持
 *
 * @author jianglei
 */
public abstract class ErrorTagSupport extends TagSupport {

    private static final long serialVersionUID = -1567572110106962210L;

    protected String field = Strings.ASTERISK;
    private String code;

    public void setField(String field) {
        this.field = field;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @SuppressWarnings("unchecked")
    protected List<ExceptionError> getErrors() {
        ServletRequest request = this.pageContext.getRequest();
        return (List<ExceptionError>) request.getAttribute(ResolvableExceptionMessageSaver.ATTRIBUTE);
    }

    protected final boolean matches(boolean inverse) {
        if (inverse) {
            return !hasError();
        } else {
            return hasError();
        }
    }

    private boolean hasError() {
        List<ExceptionError> errors = getErrors();
        if (errors != null) {
            if (Strings.ASTERISK.equals(this.field)) {
                if (StringUtils.isBlank(this.code)) {
                    return errors.size() > 0;
                } else {
                    for (ExceptionError error : errors) {
                        if (this.code.equals(error.getCode())) {
                            return true;
                        }
                    }
                }
            }
            for (ExceptionError error : errors) {
                if (this.field.equals(error.getField())) {
                    return StringUtils.isBlank(this.code) || this.code.equals(error.getCode());
                }
            }
        }
        return false;
    }

}
