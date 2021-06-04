package org.truenewx.tnxjee.service.exception.message;

import java.util.Locale;

import org.truenewx.tnxjee.service.exception.model.CodedError;

/**
 * 具有编码错误的解决器
 *
 * @author jianglei
 */
public interface CodedErrorResolver {

    CodedError resolveError(String code, Locale locale, Object... args);

}
