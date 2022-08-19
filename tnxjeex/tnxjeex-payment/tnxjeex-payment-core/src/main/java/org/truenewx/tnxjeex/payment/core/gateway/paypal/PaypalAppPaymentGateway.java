package org.truenewx.tnxjeex.payment.core.gateway.paypal;

import org.truenewx.tnxjee.core.http.HttpRequestDataProvider;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjee.model.spec.enums.Program;
import org.truenewx.tnxjeex.payment.core.PaymentDefinition;
import org.truenewx.tnxjeex.payment.core.PaymentRequestParameter;
import org.truenewx.tnxjeex.payment.core.PaymentResult;
import org.truenewx.tnxjeex.payment.core.gateway.PaymentChannel;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

/**
 * PayPal 贝宝APP支付网关
 *
 * @author jianglei
 */
public class PaypalAppPaymentGateway extends PaypalPaymentGateway {

    public PaypalAppPaymentGateway() {
        setTerminals(new Terminal(Program.NATIVE, null, null));
    }

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.PAYPAL;
    }

    @Override
    public PaymentRequestParameter getRequestParameter(PaymentDefinition definition) {
        return null;
    }

    @Override
    public PaymentResult getResult(HttpRequestDataProvider notifyDataProvider) {
        APIContext apiContext = new APIContext(getClientId(), getClientSecret(), getMode());
        String paymentId = notifyDataProvider.getParameter("paymentId");
        try {
            return toPaymentResult(Payment.get(apiContext, paymentId));
        } catch (PayPalRESTException e) {
            this.logger.error(e.getMessage(), e);
        }
        return null;
    }

}
