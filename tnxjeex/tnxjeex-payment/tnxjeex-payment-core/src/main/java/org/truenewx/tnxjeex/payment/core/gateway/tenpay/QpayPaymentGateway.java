package org.truenewx.tnxjeex.payment.core.gateway.tenpay;

import org.truenewx.tnxjeex.payment.core.gateway.PaymentChannel;

/**
 * 支付网关：QQ钱包
 *
 * @author jianglei
 */
public class QpayPaymentGateway extends TenpayPaymentGateway {

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.QPAY;
    }

}
