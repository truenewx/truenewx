package org.truenewx.tnxjeex.payment.service.gateway.paypal;

import java.math.BigDecimal;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjeex.payment.gateway.AbstractPaymentGateway;
import org.truenewx.tnxjeex.payment.model.PaymentChannel;
import org.truenewx.tnxjeex.payment.model.PaymentResult;

import com.paypal.api.payments.*;

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

    protected PaymentResult toPaymentResult(Payment executeResult) {
        this.logger.info(executeResult.toJSON());
        if ("approved".equals(executeResult.getState())) {
            Transaction transaction = executeResult.getTransactions().get(0);
            RelatedResources relatedResources = transaction.getRelatedResources().get(0);
            Sale sale = relatedResources.getSale();

            String gatewayPaymentNo = sale.getId();
            BigDecimal amount = MathUtil.parseDecimal(sale.getAmount().getTotal());
            Item item = transaction.getItemList().getItems().get(0);
            String orderNo = item.getSku();

            return new PaymentResult(gatewayPaymentNo, orderNo, amount, Strings.EMPTY);
        }
        return null;
    }
}
