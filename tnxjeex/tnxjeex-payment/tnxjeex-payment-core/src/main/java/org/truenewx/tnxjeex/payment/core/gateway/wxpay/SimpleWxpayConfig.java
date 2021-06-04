package org.truenewx.tnxjeex.payment.core.gateway.wxpay;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.Resource;

/**
 * 简单的微信支付配置
 *
 * @author jianglei
 */
public class SimpleWxpayConfig implements WXPayConfig, Cloneable {

    private String appId;
    private String merchantId;
    private String apiKey;
    private Resource cert;

    @Override
    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    public String getMerchantId() {
        return this.merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    @Override
    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setCert(Resource cert) {
        this.cert = cert;
    }

    @Override
    public InputStream getCertStream() {
        try {
            return this.cert.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {

            @Override
            public void report(String domain, long elapsedTimeMillis, Exception ex) {

            }

            @Override
            public DomainInfo getDomain(WXPayConfig config) {
                return new DomainInfo(WXPayConstants.DOMAIN_API, true);
            }

        };
    }

    @Override
    public SimpleWxpayConfig clone() {
        SimpleWxpayConfig config = new SimpleWxpayConfig();
        config.appId = this.appId;
        config.merchantId = this.merchantId;
        config.apiKey = this.apiKey;
        config.cert = this.cert;
        return config;
    }

}
