package org.truenewx.tnxjeex.cas.client.validation;

import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.validation.AbstractUrlBasedTicketValidator;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ExceptionUtil;
import org.truenewx.tnxjee.core.util.HttpClientUtil;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.core.util.tuple.Binate;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.webmvc.exception.parser.ResolvableExceptionParser;
import org.truenewx.tnxjeex.cas.core.validation.SimpleAssertion;

/**
 * 基于JSON数据格式的CAS服务票据校验器
 */
public class CasJsonServiceTicketValidator extends AbstractUrlBasedTicketValidator {

    @Autowired
    private ResolvableExceptionParser exceptionParser;

    public CasJsonServiceTicketValidator(String casServerUrlPrefix) {
        super(casServerUrlPrefix);
    }

    @Override
    protected String getUrlSuffix() {
        return "serviceValidate";
    }

    @Override
    protected String retrieveResponseFromServer(URL validationUrl, String ticket) {
        try {
            Binate<Integer, String> result = HttpClientUtil.requestByGet(validationUrl.toString(), null, null);
            if (result != null) {
                return result.getLeft() + Strings.COLON + result.getRight();
            }
        } catch (Exception e) {
            throw ExceptionUtil.toRuntimeException(e);
        }
        return null;
    }

    @Override
    protected Assertion parseResponseFromServer(String response) throws TicketValidationException {
        Assertion assertion = null;
        if (StringUtils.isNotBlank(response)) {
            int index = response.indexOf(Strings.COLON);
            int statusCode = MathUtil.parseInt(response.substring(0, index));
            String content = response.substring(index + 1);
            if (statusCode == HttpServletResponse.SC_FORBIDDEN) {
                ResolvableException exception = this.exceptionParser.parse(content);
                if (exception != null) {
                    throw new TicketValidationException(exception);
                }
            } else {
                assertion = JsonUtil.json2Bean(content, SimpleAssertion.class);
            }
        }
        if (assertion == null) {
            throw new TicketValidationException("The service ticket is invalid");
        }
        if (!assertion.isValid()) {
            throw new TicketValidationException("The service ticket is expired");
        }
        return assertion;
    }

}
