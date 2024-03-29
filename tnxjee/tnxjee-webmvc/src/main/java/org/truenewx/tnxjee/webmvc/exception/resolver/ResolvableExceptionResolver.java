package org.truenewx.tnxjee.webmvc.exception.resolver;

import java.util.Set;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.service.exception.*;
import org.truenewx.tnxjee.webmvc.exception.message.ResolvableExceptionMessageSaver;

/**
 * 可解决异常解决器
 */
public abstract class ResolvableExceptionResolver extends AbstractHandlerExceptionResolver {

    @Autowired
    protected ResolvableExceptionMessageSaver messageSaver;

    public static boolean supports(Exception ex) {
        ex = prepare(ex);
        return ex instanceof ConstraintViolationException || ex instanceof ResolvableException;
    }

    private static Exception prepare(Exception ex) {
        if (ex instanceof TransactionSystemException) {
            Throwable cause;
            do {
                cause = ex.getCause();
                if (cause instanceof Exception) {
                    ex = (Exception) cause;
                    if (ex instanceof ConstraintViolationException || ex instanceof ResolvableException) {
                        break;
                    }
                }
            } while (cause != null);
        }
        return ex;
    }

    @Override
    protected final ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        ex = prepare(ex);
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) ex;
            Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
            if (violations != null && violations.size() > 0) {
                if (violations.size() == 1) {
                    ConstraintViolation<?> violation = CollectionUtil.getFirst(violations, null);
                    ex = buildFormatException(violation, request);
                } else {
                    MultiException me = new MultiException();
                    for (ConstraintViolation<?> violation : violations) {
                        me.add(buildFormatException(violation, request));
                    }
                    ex = me;
                }
            }
        }
        if (ex instanceof ResolvableException) {
            HandlerMethod handlerMethod = (handler instanceof HandlerMethod) ? (HandlerMethod) handler : null;
            if (supports(request, handlerMethod)) {
                ResolvableException re = (ResolvableException) ex;
                this.messageSaver.saveMessage(request, response, handlerMethod, re);
                return getResult(request, response, handlerMethod, re);
            }
        }
        return null;
    }

    private FormatException buildFormatException(ConstraintViolation<?> violation, HttpServletRequest request) {
        String code = violation.getMessageTemplate().replace("{", "").replace("}", "");
        String property = violation.getPropertyPath().toString();
        return new FormatException(code, violation.getRootBeanClass(), property);
    }

    protected abstract boolean supports(HttpServletRequest request, @Nullable HandlerMethod handlerMethod);

    protected abstract ModelAndView getResult(HttpServletRequest request,
            HttpServletResponse response, @Nullable HandlerMethod handlerMethod, ResolvableException re);

    @Override
    protected String buildLogMessage(Exception ex, HttpServletRequest request) {
        if (ex instanceof SingleException) {
            return buildLogMessage((SingleException) ex);
        } else if (ex instanceof MultiException) {
            MultiException me = (MultiException) ex;
            StringBuilder message = new StringBuilder();
            me.forEach(se -> {
                String singleMessage = buildLogMessage(se);
                if (StringUtils.isNotBlank(singleMessage)) {
                    message.append(singleMessage).append(Strings.ENTER);
                }
            });
            return message.toString().trim();
        }
        return super.buildLogMessage(ex, request);
    }

    private String buildLogMessage(SingleException se) {
        if (se instanceof BusinessException) {
            return buildLogMessage((BusinessException) se);
        } else if (se instanceof FormatException) {
            return buildLogMessage((FormatException) se);
        }
        return null;
    }

    private String buildLogMessage(BusinessException be) {
        StringBuilder message = new StringBuilder("====== ").append(be.getCode());
        String args = StringUtils.join(be.getArgs(), Strings.COMMA);
        if (StringUtils.isNotBlank(args)) {
            message.append(Strings.COLON).append(args);
        }
        if (be.isBoundProperty()) {
            message.append(Strings.LEFT_BRACKET).append(be.getProperty()).append(Strings.RIGHT_BRACKET);
        }
        message.append(" ======");
        return message.toString();
    }

    private String buildLogMessage(FormatException fe) {
        return "====== " + fe.getCode() + Strings.LEFT_BRACKET + fe.getModelClass().getName() + Strings.DOT + fe
                .getProperty() + Strings.RIGHT_BRACKET + " ======";
    }

}
