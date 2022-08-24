package org.truenewx.tnxjeex.payment.gateway;

import org.springframework.core.Ordered;
import org.truenewx.tnxjee.core.spec.Named;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjeex.payment.model.PaymentChannel;

/**
 * 支付网关
 *
 * @author jianglei
 */
public interface PaymentGateway extends Named, Ordered {

    boolean isActive();

    PaymentChannel getChannel();

    String getNationCode();

    Terminal[] getTerminals();

    boolean isRefundable();

}
