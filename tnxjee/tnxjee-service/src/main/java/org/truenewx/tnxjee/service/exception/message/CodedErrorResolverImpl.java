package org.truenewx.tnxjee.service.exception.message;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.message.MessageResolver;
import org.truenewx.tnxjee.service.exception.model.CodedError;

/**
 * 具有编码错误的解决器实现
 *
 * @author jianglei
 */
@Component
public class CodedErrorResolverImpl implements CodedErrorResolver {

    @Autowired
    private MessageResolver messageResolver;

    @Override
    public CodedError resolveError(String code, Locale locale, Object... args) {
        String message = this.messageResolver.resolveMessage(code, locale, args);
        return new CodedError(code, message);
    }

}
