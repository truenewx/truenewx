package org.truenewx.tnxjee.webmvc.exception.message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.service.exception.MultiException;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.service.exception.SingleException;
import org.truenewx.tnxjee.service.exception.message.SingleExceptionMessageResolver;
import org.truenewx.tnxjee.service.exception.model.ExceptionError;
import org.truenewx.tnxjee.webmvc.exception.model.ExceptionErrorBody;
import org.truenewx.tnxjee.webmvc.util.SpringWebMvcUtil;

/**
 * 可解决异常消息保存器实现
 */
@Component
public class ResolvableExceptionMessageSaverImpl implements ResolvableExceptionMessageSaver {

    @Autowired
    private SingleExceptionMessageResolver resolver;

    @Override
    public void saveMessage(HttpServletRequest request, HttpServletResponse response,
            HandlerMethod handlerMethod, ResolvableException re) {
        List<ExceptionError> errors = buildErrors(re, request.getLocale());
        if (errors.size() > 0) {
            if (SpringWebMvcUtil.isResponseBody(handlerMethod)) {
                try {
                    ExceptionErrorBody body = new ExceptionErrorBody(errors);
                    String json = JsonUtil.toJson(body);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.getWriter().print(json);
                } catch (IOException e) {
                    LogUtil.error(getClass(), e);
                }
            } else {
                request.setAttribute(ATTRIBUTE, errors);
            }
        }
    }

    private List<ExceptionError> buildErrors(ResolvableException re, Locale locale) {
        List<ExceptionError> errors = new ArrayList<>();
        if (re instanceof SingleException) {
            errors.add(buildError((SingleException) re, locale));
        } else if (re instanceof MultiException) {
            for (SingleException se : (MultiException) re) {
                errors.add(buildError(se, locale));
            }
        }
        return errors;
    }

    private ExceptionError buildError(SingleException se, Locale locale) {
        String message = this.resolver.resolveMessage(se, locale);
        return new ExceptionError(se, message);
    }

}
