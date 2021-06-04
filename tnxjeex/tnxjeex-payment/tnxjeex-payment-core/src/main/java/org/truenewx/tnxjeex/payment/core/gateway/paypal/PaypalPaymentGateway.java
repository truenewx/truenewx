package org.truenewx.tnxjeex.payment.core.gateway.paypal;

import org.truenewx.tnxjeex.payment.core.gateway.AbstractPaymentGateway;
import org.truenewx.tnxjeex.payment.core.gateway.PaymentChannel;

/**
 * PayPal 贝宝支付网关
 *
 * @author jianglei
 */
public abstract class PaypalPaymentGateway extends AbstractPaymentGateway {

    private String clientId;
    private String clientSecret;
    private String mode;

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    protected String getClientId() {
        return this.clientId;
    }

    protected String getClientSecret() {
        return this.clientSecret;
    }

    protected String getMode() {
        return this.mode;
    }

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.PAYPAL;
    }
}
