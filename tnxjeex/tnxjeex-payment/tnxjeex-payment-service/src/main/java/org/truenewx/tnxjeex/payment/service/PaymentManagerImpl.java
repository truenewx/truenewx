package org.truenewx.tnxjeex.payment.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.http.HttpRequestDataProvider;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjeex.payment.model.PaymentDefinition;
import org.truenewx.tnxjeex.payment.model.PaymentRequestParameter;
import org.truenewx.tnxjeex.payment.model.PaymentResult;
import org.truenewx.tnxjeex.payment.service.gateway.PaymentGateway;
import org.truenewx.tnxjeex.payment.service.gateway.PaymentGatewayAdapter;

/**
 * 支付管理器实现
 *
 * @author jianglei
 */
@Service
public class PaymentManagerImpl implements PaymentManager, ContextInitializedBean {

    private Map<String, PaymentGatewayAdapter> gateways = new HashMap<>();
    @Autowired(required = false)
    private PaymentListener listener;

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        Map<String, PaymentGatewayAdapter> adapters = context.getBeansOfType(PaymentGatewayAdapter.class);
        for (PaymentGatewayAdapter gateway : adapters.values()) {
            String name = gateway.getName();
            Assert.isNull(this.gateways.put(name, gateway), "More than one gateway named " + name);
        }
    }

    @Override
    public List<PaymentGateway> getGateways(Terminal terminal) {
        List<PaymentGateway> gateways = new ArrayList<>();
        for (PaymentGateway gateway : this.gateways.values()) {
            Terminal[] terminals = gateway.getTerminals();
            if (terminals != null) {
                for (Terminal t : terminals) {
                    if (t.supports(terminal)) {
                        gateways.add(gateway);
                    }
                }
            }
        }
        return gateways;
    }

    @Override
    public PaymentGateway getGateway(String gatewayName) {
        return this.gateways.get(gatewayName);
    }

    @Override
    public PaymentRequestParameter getRequestParameter(String gatewayName, PaymentDefinition definition) {
        PaymentGatewayAdapter adapter = this.gateways.get(gatewayName);
        if (adapter != null) {
            return adapter.getRequestParameter(definition);
        }
        return null;
    }

    @Override
    public PaymentResult notifyResult(String gatewayName, boolean confirmed,
            HttpRequestDataProvider notifyDataProvider) {
        PaymentGatewayAdapter adapter = this.gateways.get(gatewayName);
        if (adapter != null) {
            PaymentResult result = adapter.getResult(notifyDataProvider);
            if (confirmed && result != null && result.isSuccessful() && this.listener != null) {
                this.listener.onPaid(adapter.getChannel(), result.getGatewayPaymentNo(), result.getOrderNo(),
                        result.getAmount());
            }
            return result;
        }
        return null;
    }

    @Override
    public void requestRefund(String gatewayName, String gatewayPaymentNo, BigDecimal paymentAmount, String refundNo,
            String refundAmount) {
        PaymentGatewayAdapter adapter = this.gateways.get(gatewayName);
        if (adapter != null) {
            String gatewayRefundNo = adapter.requestRefund(gatewayPaymentNo, paymentAmount, refundNo, refundAmount);
            if (this.listener != null) {
                this.listener.onRefunded(refundNo, gatewayRefundNo);
            }
        }
    }

}
