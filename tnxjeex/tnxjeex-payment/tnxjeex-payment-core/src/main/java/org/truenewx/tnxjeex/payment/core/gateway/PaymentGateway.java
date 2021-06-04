package org.truenewx.tnxjeex.payment.core.gateway;

import org.truenewx.tnxjee.core.spec.Named;
import org.truenewx.tnxjee.model.spec.Terminal;

/**
 * 支付网关
 *
 * @author jianglei
 */
public interface PaymentGateway extends Named {

    boolean isActive();

    PaymentChannel getChannel();

    String getNationCode();

    Terminal[] getTerminals();

    String getLogoUrl();

    boolean isRefundable();

}
