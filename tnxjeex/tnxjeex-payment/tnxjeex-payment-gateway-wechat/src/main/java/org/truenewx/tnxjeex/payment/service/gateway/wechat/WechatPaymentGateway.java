package org.truenewx.tnxjeex.payment.service.gateway.wechat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Base64Utils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.http.HttpRequestDataProvider;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjee.model.spec.enums.Device;
import org.truenewx.tnxjee.model.spec.enums.OS;
import org.truenewx.tnxjeex.payment.model.PaymentChannel;
import org.truenewx.tnxjeex.payment.model.PaymentDefinition;
import org.truenewx.tnxjeex.payment.model.PaymentRequestParameter;
import org.truenewx.tnxjeex.payment.model.PaymentResult;
import org.truenewx.tnxjeex.payment.service.gateway.AbstractPaymentGateway;

/**
 * 支付网关：微信支付
 *
 * @author jianglei
 */
public class WechatPaymentGateway extends AbstractPaymentGateway {

    /**
     * 下单地址前缀
     */
    private static final String TRANS_URL_PREFIX = "https://api.mch.weixin.qq.com/v3/pay/transactions/";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    private final WechatPayClient client;
    private final WechatPayPlatformCertManager platformCertManager;

    public WechatPaymentGateway(WechatPayClient client) {
        this.client = client;
        this.platformCertManager = new WechatPayPlatformCertManager(client);
    }

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.WECHAT;
    }

    @Override
    public PaymentRequestParameter getRequestParameter(PaymentDefinition definition) {
        Terminal terminal = definition.getTerminal();
        WechatPayProduct product = getProduct(terminal);
        if (product != null) {
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("mchid", this.client.getMerchantId()); // 商户id
            requestParams.put("appid", this.client.getAppId()); // 应用id
            requestParams.put("out_trade_no", definition.getOrderNo()); // 商户订单号
            requestParams.put("description", definition.getDescription()); // 商品描述
            requestParams.put("notify_url", getResultConfirmUrl()); // 通知地址

            Map<String, Object> amount = new HashMap<>();
            amount.put("total", definition.getAmount().multiply(BigDecimal.valueOf(100)).intValue()); // 金额
            Currency currency = definition.getCurrency();
            if (currency == null) {
                currency = Currency.getInstance(Locale.getDefault());
            }
            amount.put("currency", currency.getCurrencyCode()); // 币种
            requestParams.put("amount", amount);

            if (product == WechatPayProduct.JSAPI) { // 微信内嵌支付时，用户openId必填
                Map<String, Object> payer = new HashMap<>();
                payer.put("openid", definition.getTarget());
                requestParams.put("payer", payer);
            } else if (product == WechatPayProduct.H5) { // 微信外部移动网页支付时，用户终端IP和H5场景类型必填
                Map<String, Object> scene = new HashMap<>();
                scene.put("payer_client_ip", definition.getPayerIp());
                Map<String, Object> h5 = new HashMap<>();
                h5.put("type", getH5Type(terminal.getOs()));
                scene.put("h5_info", h5);
                requestParams.put("scene_info", scene);
            }
            return request(product, requestParams);
        }
        return null;
    }

    private WechatPayProduct getProduct(Terminal terminal) {
        switch (terminal.getProgram()) {
            case WEB:
                // PC端网页使用二维码扫码支付
                if (terminal.getDevice() == Device.PC) {
                    return WechatPayProduct.NATIVE;
                }
                // 移动端（不论手机还是平板）网页使用H5方式
                return WechatPayProduct.H5;
            case NATIVE: // 原生APP
                return WechatPayProduct.APP;
            case MINI: // 微信小程序
            case HYBRID: // 微信内嵌网页
                return WechatPayProduct.JSAPI;
            default:
                return null;
        }
    }

    private String getH5Type(OS os) {
        if (os == OS.MAC) {
            return "iOS";
        }
        if (os == OS.ANDROID) {
            return "Android";
        }
        return "Wap";
    }

    private PaymentRequestParameter request(WechatPayProduct product, Map<String, Object> requestParams) {
        try {
            String uri = TRANS_URL_PREFIX + product.name().toLowerCase();
            HttpResponse response = this.client.post(uri, requestParams);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_ACCEPTED) { // 202，服务器已接受请求，但尚未处理；使用原参数重复请求一遍
                return request(product, requestParams);
            }
            String responseJson = EntityUtils.toString(response.getEntity());
            Map<String, Object> responseData = JsonUtil.json2Map(responseJson);
            if (statusCode == HttpStatus.SC_OK) {
                if (product == WechatPayProduct.NATIVE) {
                    PaymentRequestParameter result = new PaymentRequestParameter();
                    result.setUrl((String) responseData.get("code_url"));
                    return result;
                }
                Map<String, String> callParams = new LinkedHashMap<>();
                callParams.put("appId", this.client.getAppId());
                callParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
                callParams.put("nonceStr", StringUtil.uuid32());
                callParams.put("package", "prepay_id=" + responseData.get("prepay_id"));
                callParams.put("paySign", generateSignature(callParams.values(), this.client.getCertPrivateKey()));
                callParams.put("signType", "RSA"); // 不参与签名
                return new PaymentRequestParameter(callParams);
            }
            String errorMessage = (String) responseData.get("message");
            throw new RuntimeException(errorMessage);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private String generateSignature(Collection<String> data, PrivateKey privateKey) {
        StringBuilder token = new StringBuilder();
        for (String value : data) {
            token.append(value).append(Strings.ENTER);
        }
        try {
            Signature signer = Signature.getInstance(SIGNATURE_ALGORITHM);
            signer.initSign(privateKey);
            signer.update(token.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signer.sign());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public PaymentResult getResult(HttpRequestDataProvider notifyDataProvider) {
        try {
            String certSerialNo = notifyDataProvider.getHeader("Wechatpay-Serial");
            Certificate platformCert = this.platformCertManager.getCert(certSerialNo);

            String signature = notifyDataProvider.getHeader("Wechatpay-Signature");
            String timestamp = notifyDataProvider.getHeader("Wechatpay-Timestamp");
            String nonce = notifyDataProvider.getHeader("Wechatpay-Nonce");
            String body = notifyDataProvider.getBody();
            String token = timestamp + Strings.ENTER + nonce + Strings.ENTER + body + Strings.ENTER;

            Signature signer = Signature.getInstance(SIGNATURE_ALGORITHM);
            signer.initVerify(platformCert);
            signer.update(token.getBytes(StandardCharsets.UTF_8));
            if (signer.verify(Base64Utils.decodeFromString(signature))) {
                Map<String, Object> bodyMap = JsonUtil.json2Map(body);
                Map<String, Object> resource = (Map<String, Object>) bodyMap.get("resource");
                String resourceJson = this.platformCertManager.decryptResource(resource);
                resource = JsonUtil.json2Map(resourceJson);

                String tradeState = (String) resource.get("trade_state");
                if ("SUCCESS".equals(tradeState)) {
                    String gatewayPaymentNo = (String) resource.get("transaction_id");
                    String orderNo = (String) resource.get("out_trade_no");
                    Map<String, Object> amount = (Map<String, Object>) resource.get("amount");
                    BigDecimal amountTotal = new BigDecimal((Integer) amount.get("total"))
                            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                    return new PaymentResult(gatewayPaymentNo, orderNo, amountTotal, Strings.EMPTY);
                }
            }
        } catch (Exception e) {
            Map<String, Object> error = Map.of("code", "FAIL", "message", e.getMessage());
            return new PaymentResult(HttpStatus.SC_INTERNAL_SERVER_ERROR, JsonUtil.toJson(error));
        }
        return null;
    }

}