package org.truenewx.tnxjeex.payment.core.gateway.wxpay;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.truenewx.tnxjee.core.http.HttpRequestDataProvider;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.payment.core.PaymentDefinition;
import org.truenewx.tnxjeex.payment.core.PaymentRequestParameter;
import org.truenewx.tnxjeex.payment.core.PaymentResult;
import org.truenewx.tnxjeex.payment.core.gateway.AbstractPaymentGateway;
import org.truenewx.tnxjeex.payment.core.gateway.PaymentChannel;

/**
 * 支付网关：微信支付
 *
 * @author jianglei
 */
public class WxpayPaymentGateway extends AbstractPaymentGateway {

    private WxPay wxpay;
    private String tradeType;

    public WxpayPaymentGateway(SimpleWxpayConfig config, boolean useSandbox) throws Exception {
        WxPay wxpay = new WxPay(config, false, useSandbox);
        if (useSandbox) {
            SimpleWxpayConfig sandboxConfig = config.clone();
            sandboxConfig.setApiKey(getSandboxSignKey(wxpay));
            wxpay = new WxPay(sandboxConfig, false, true);
        }
        this.wxpay = wxpay;
    }

    /**
     * 设置交易类型，取值范围：JSAPI（微信内网页或小程序），NATIVE（二维码），APP（商户APP）
     *
     * @param tradeType 交易类型
     */
    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    private static String getSandboxSignKey(WxPay wxpay) throws Exception {
        WxPayConfig config = wxpay.getConfig();
        Map<String, String> reqData = new HashMap<>();
        reqData.put("mch_id", config.getMerchantId());
        reqData.put("nonce_str", WxPayUtil.generateNonceStr());
        reqData.put("sign", WxPayUtil.generateSignature(reqData, config.getApiKey(), wxpay.getSignType()));
        String response = wxpay.requestWithoutCert("/sandboxnew/pay/getsignkey", reqData, 3000, 3000);
        Map<String, String> result = WxPayUtil.xmlToMap(response);
        return result.get("sandbox_signkey");
    }

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.WECHAT;
    }

    @Override
    public PaymentRequestParameter getRequestParameter(PaymentDefinition definition) {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("out_trade_no", definition.getOrderNo()); // 商户订单号
        requestData.put("body", definition.getDescription()); // 商品描述
        requestData.put("fee_type", definition.getCurrency().getCurrencyCode()); // 币种
        int amount = definition.getAmount().multiply(BigDecimal.valueOf(100)).intValue();
        requestData.put("total_fee", String.valueOf(amount)); // 金额
        requestData.put("spbill_create_ip", definition.getPayerIp()); // 客户终端IP
        requestData.put("notify_url", getResultConfirmUrl()); // 通知地址
        requestData.put("trade_type", this.tradeType); // 支付类型
        if ("NATIVE".equals(this.tradeType)) { // 二维码支付时，商品id必填
            requestData.put("product_id", definition.getTarget());
        } else if ("JSAPI".equals(this.tradeType)) { // 微信内页面支付时，用户openId必填
            requestData.put("openid", definition.getTarget());
        }

        try {
            Map<String, String> responseData = this.wxpay.unifiedOrder(requestData);
            if (!WxPayConstants.SUCCESS.equals(responseData.get("return_code"))) {
                throw new RuntimeException(responseData.get("return_msg"));
            }
            Map<String, String> params = new LinkedHashMap<>();
            WxPayConfig config = this.wxpay.getConfig();
            params.put("appId", config.getAppId());
            params.put("timeStamp", String.valueOf(WxPayUtil.getCurrentTimestamp()));
            params.put("nonceStr", WxPayUtil.generateNonceStr());
            params.put("package", "prepay_id=" + responseData.get("prepay_id"));
            params.put("signType", this.wxpay.getSignTypeValue());
            params.put("paySign", WxPayUtil.generateSignature(params, config.getApiKey(), this.wxpay.getSignType()));
            // 该场景下JS客户端由JSAPI库执行提交，不需要服务端指定提交地址和提交方式
            return new PaymentRequestParameter(params);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public PaymentResult getResult(HttpRequestDataProvider notifyDataProvider)
            throws BusinessException {
        if (WxPayConstants.SUCCESS.equals(notifyDataProvider.getParameter("return_code"))) {
            try {
                Map<String, String> params = CollectionUtil.toStringMap(notifyDataProvider.getParameters());
                if (WxPayUtil.isSignatureValid(params, this.wxpay.getConfig().getApiKey(), this.wxpay.getSignType())) {
                    String gatewayPaymentNo = notifyDataProvider.getParameter("transaction_id");
                    BigDecimal amount = new BigDecimal(notifyDataProvider.getParameter("total_fee"))
                            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                    String orderNo = notifyDataProvider.getParameter("out_trade_no");
                    Map<String, String> responseData = new LinkedHashMap<>();
                    responseData.put("return_code", WxPayConstants.SUCCESS);
                    responseData.put("return_msg", "OK");
                    String response = WxPayUtil.mapToXml(responseData);
                    return new PaymentResult(gatewayPaymentNo, orderNo, amount, response);
                }
            } catch (Exception e) {
                this.logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

}
