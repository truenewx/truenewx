package org.truenewx.tnxjeex.payment.core.gateway.alipay;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjee.model.spec.enums.Program;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.spec.region.RegionNationCodes;
import org.truenewx.tnxjeex.payment.core.PaymentDefinition;
import org.truenewx.tnxjeex.payment.core.PaymentRequestParameter;
import org.truenewx.tnxjeex.payment.core.gateway.PaymentExceptionCodes;

/**
 * 支付宝APP支付网关
 *
 * @author jianglei
 */
public class AlipayAppPaymentGateway extends AlipayPaymentGateway {

    private String privateKey;
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

    public AlipayAppPaymentGateway() {
        setTerminals(new Terminal(Program.NATIVE, null, null));
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public PaymentRequestParameter getRequestParameter(PaymentDefinition definition) {
        SortedMap<String, String> params = new TreeMap<>();
        params.put("_input_charset", Strings.ENCODING_UTF8.toLowerCase());
        params.put("appenv", definition.getTerminal().getOs().toString().toLowerCase());
        params.put("body", definition.getDescription());
        params.put("currency", definition.getCurrency().toString());
        params.put("notify_url", this.getResultConfirmUrl());
        if (RegionNationCodes.HONG_KONG.equals(getNationCode())) {
            params.put("forex_biz", "FP");// 官方未有表明,客服确认用于标识境外移动端
            params.put("payment_inst", "ALIPAYHK");// 表明需要打开海外支付宝版本
            params.put("product_code", "NEW_WAP_OVERSEAS_SELLER");// 用来区分新境外收单还是旧版本的境外收单
        }
        params.put("out_trade_no", definition.getOrderNo());
        params.put("partner", this.partner);
        params.put("payment_type", "1");
        params.put("seller_id", this.partner);
        params.put("service", "mobile.securitypay.pay");
        params.put("subject", definition.getDescription());
        params.put("total_fee", definition.getAmount().toString());
        sign(params);
        return new PaymentRequestParameter(params);
    }

    @Override
    protected void sign(SortedMap<String, String> params) {
        StringBuffer sb = new StringBuffer();
        Set<Entry<String, String>> entrySet = params.entrySet();
        for (Entry<String, String> entry : entrySet) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (StringUtils.isNotBlank(v) && !"sign".equals(k) && !"key".equals(k) && !"sign_type".equals(k)) {
                sb.append(k + "=\"" + v + "\"&");
            }
        }
        try {
            String sign = RSA.sign(sb.substring(0, sb.lastIndexOf("&")), this.privateKey, Strings.ENCODING_UTF8);
            params.put("sign", sign);
            params.put("sign_type", "RSA");
            params.put("request_content", sb.substring(0, sb.lastIndexOf("&")) + "&sign=\""
                    + URLEncoder.encode(sign, Strings.ENCODING_UTF8) + "\"" + "&sign_type=\"RSA\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void validateSign(Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            String v = params.get(k);
            if (StringUtils.isNotBlank(v) && !"sign".equals(k) && !"key".equals(k) && !"sign_type".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }

        if (!RSA.verify(sb.substring(0, sb.lastIndexOf("&")), params.get("sign"), this.publicKey,
                Strings.ENCODING_UTF8)) {
            throw new BusinessException(PaymentExceptionCodes.SIGN_FAIL);
        }
    }
}
