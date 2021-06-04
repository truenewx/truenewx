package org.truenewx.tnxjeex.payment.core.gateway.tenpay;

import org.truenewx.tnxjeex.payment.core.gateway.PaymentChannel;

/**
 * 支付网关：微信
 *
 * @author jianglei
 */
public class WeixinPaymentGateway extends TenpayPaymentGateway {

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.WEIXIN;
    }

}
