package org.truenewx.tnxjeex.cas.client.validation;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.validation.AbstractCasProtocolUrlBasedTicketValidator;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjeex.cas.core.validation.SimpleAssertion;

/**
 * 基于JSON数据格式的CAS服务票据校验器
 */
public class CasJsonServiceTicketValidator extends AbstractCasProtocolUrlBasedTicketValidator {

    public CasJsonServiceTicketValidator(String casServerUrlPrefix) {
        super(casServerUrlPrefix);
    }

    @Override
    protected String getUrlSuffix() {
        return "serviceValidate";
    }

    @Override
    protected Assertion parseResponseFromServer(String response) throws TicketValidationException {
        Assertion assertion = null;
        if (StringUtils.isNotBlank(response)) {
            assertion = JsonUtil.json2Bean(response, SimpleAssertion.class);
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
