package org.truenewx.tnxjeex.payment.service.gateway.paypal;

import java.util.ArrayList;
import java.util.List;

import org.truenewx.tnxjee.core.http.HttpRequestDataProvider;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjee.model.spec.enums.Program;
import org.truenewx.tnxjeex.payment.model.PaymentDefinition;
import org.truenewx.tnxjeex.payment.model.*;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

/**
 * PayPal 贝宝WEB支付网关
 *
 * @author jianglei
 */
public class PaypalWebPaymentGateway extends PaypalPaymentGateway {

    public PaypalWebPaymentGateway() {
        setTerminals(new Terminal(Program.WEB, null, null));
    }

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.PAYPAL;
    }

    @Override
    public PaymentRequest prepareRequest(PaymentDefinition definition) {
        // 建立金额与币种
        Amount amountDetail = new Amount();
        amountDetail.setCurrency(definition.getCurrency().toString());
        amountDetail.setTotal(definition.getAmount().toString());
        Transaction transaction = new Transaction();
        transaction.setAmount(amountDetail);
        transaction.setInvoiceNumber(definition.getOrderNo());
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        // 建立支付方式
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setNoteToPayer(definition.getDescription());

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(getResultShowUrl());
        redirectUrls.setReturnUrl(getResultConfirmUrl());// 回调路径
        payment.setRedirectUrls(redirectUrls);
        try {
            APIContext apiContext = new APIContext(getClientId(), getClientSecret(), getMode());
            Payment createdPayment = payment.create(apiContext);
            for (Links links : createdPayment.getLinks()) {
                if ("approval_url".equals(links.getRel())) {
                    String url = links.getHref();
                    return new PaymentRequest(url, PaymentRequestMode.GET, null);
                }
            }
        } catch (PayPalRESTException e) {
            this.logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public PaymentResult parseResult(HttpRequestDataProvider notifyDataProvider) {
        Payment payment = new Payment();
        payment.setId(notifyDataProvider.getParameter("paymentId"));
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(notifyDataProvider.getParameter("PayerID"));
        try {
            APIContext apiContext = new APIContext(getClientId(), getClientSecret(), getMode());
            return toPaymentResult(payment.execute(apiContext, paymentExecution));
        } catch (PayPalRESTException e) {
            this.logger.error(e.getMessage(), e);
        }
        return null;
    }

}
