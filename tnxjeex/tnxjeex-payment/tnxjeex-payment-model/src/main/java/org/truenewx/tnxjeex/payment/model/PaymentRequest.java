package org.truenewx.tnxjeex.payment.model;

import java.util.Map;

/**
 * 支付请求
 *
 * @author jianglei
 */
public class PaymentRequest {

    private String url;
    private PaymentRequestMode mode;
    private Map<String, String> params;

    public PaymentRequest(String url, PaymentRequestMode mode, Map<String, String> params) {
        this.url = url;
        this.mode = mode;
        this.params = params;
    }

    public String getUrl() {
        return this.url;
    }


    public PaymentRequestMode getMode() {
        return this.mode;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

}
