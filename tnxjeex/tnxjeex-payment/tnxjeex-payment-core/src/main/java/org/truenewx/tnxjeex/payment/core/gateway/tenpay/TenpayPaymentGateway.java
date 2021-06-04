package org.truenewx.tnxjeex.payment.core.gateway.tenpay;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.DateUtil;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.payment.core.PaymentDefinition;
import org.truenewx.tnxjeex.payment.core.PaymentRequestParameter;
import org.truenewx.tnxjeex.payment.core.PaymentResult;
import org.truenewx.tnxjeex.payment.core.gateway.AbstractPaymentGateway;
import org.truenewx.tnxjeex.payment.core.gateway.PaymentChannel;
import org.truenewx.tnxjeex.payment.core.gateway.PaymentExceptionCodes;
import org.truenewx.tnxjeex.payment.core.gateway.RespondBusinessException;

/**
 * 支付网关：财付通
 *
 * @author jianglei
 */
public class TenpayPaymentGateway extends AbstractPaymentGateway {

    private String partner;
    private String privateKey;
    private TenpayHttpClient httpClient = new TenpayHttpClient();
    private String gateNofityFeedbackUrl = "https://gw.tenpay.com/gateway/simpleverifynotifyid.xml";

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.TENPAY;
    }

    @Override
    public PaymentRequestParameter getRequestParameter(PaymentDefinition definition) {
        SortedMap<String, String> params = new TreeMap<>();
        // 设置支付参数
        params.put("partner", this.partner); // 商户号
        params.put("notify_url", getResultConfirmUrl()); // 接收财付通通知的URL
        params.put("return_url", getResultShowUrl()); // 交易完成后跳转的URL
        // this.params.put("bank_type", "DEFAULT"); // 银行类型(中介担保时此参数无效)
        params.put("spbill_create_ip", definition.getPayerIp()); // 用户的公网ip，不是商户服务器IP
        params.put("fee_type", "1"); // 币种，1人民币

        // 系统可选参数
        params.put("sign_type", "MD5"); // 签名类型,默认：MD5
        params.put("service_version", "1.0"); // 版本号，默认为1.0
        params.put("input_charset", Strings.ENCODING_UTF8); // 字符编码
        params.put("sign_key_index", "1"); // 密钥序号

        // 业务可选参数
        params.put("attach", ""); // 附加数据，原样返回
        params.put("product_fee", ""); // 商品费用，必须保证transport_fee +
                                       // product_fee=total_fee
        params.put("transport_fee", "0"); // 物流费用，必须保证transport_fee +
                                          // product_fee=total_fee
        params.put("time_start", DateUtil.getCurrentTimeNoDelimiter()); // 订单生成时间，格式为yyyymmddhhmmss
        params.put("time_expire", ""); // 订单失效时间，格式为yyyymmddhhmmss
        params.put("buyer_id", ""); // 买方财付通账号
        params.put("goods_tag", ""); // 商品标记
        params.put("trade_mode", "1"); // 交易模式，1即时到账(默认)，2中介担保，3后台选择（买家进支付中心列表选择）
        params.put("transport_desc", ""); // 物流说明
        params.put("trans_type", "1"); // 交易类型，1实物交易，2虚拟交易
        params.put("agentid", ""); // 平台ID
        params.put("agent_type", ""); // 代理模式，0无代理(默认)，1表示卡易售模式，2表示网店模式
        params.put("seller_id", ""); // 卖家商户号，为空则等同于partner

        params.put("out_trade_no", definition.getOrderNo());
        // 商品价格（包含运费），以分为单位
        int fee = definition.getAmount().multiply(new BigDecimal(100)).intValue();
        params.put("total_fee", String.valueOf(fee));
        params.put("body", definition.getDescription());// 商品描述
        params.put("subject", definition.getDescription()); // 商品名称(中介交易时必填)
        sign(params);
        return new PaymentRequestParameter(params);
    }

    protected void sign(Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        Set<Entry<String, String>> entrySet = params.entrySet();
        for (Entry<String, String> entry : entrySet) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (StringUtils.isNotBlank(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + this.privateKey);
        String sign = EncryptUtil.encryptByMd5(sb.toString()).toLowerCase();
        params.put("sign", sign);
        // this.setDebugInfo(sb.toString() + " => sign:" + sign);
    }

    @Override
    public PaymentResult getResult(boolean confirmed, Terminal terminal, Map<String, String> params)
            throws BusinessException {
        try {
            if (confirmed) {
                return getConfirmedResult(params);
            } else {
                return getShowResult(params);
            }
        } catch (BusinessException e) {
            throw e;
        }
    }

    private PaymentResult getConfirmedResult(Map<String, String> params) throws BusinessException {
        // 通知id
        String notify_id = params.get("notify_id");
        Map<String, String> reqParams = new TreeMap<>();
        reqParams.put("partner", this.partner);
        reqParams.put("notify_id", notify_id);
        // 应答对象
        // 通信对象
        this.httpClient.setTimeOut(5);
        // 设置请求内容
        this.httpClient.setReqContent(getRequestUrl(reqParams));
        // 后台调用
        if (this.httpClient.call()) {
            ClientResponseHandler crh = new ClientResponseHandler();
            crh.setKey(this.privateKey);
            // 设置结果参数
            try {
                crh.setContent(this.httpClient.getResContent());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RespondBusinessException("fail", PaymentExceptionCodes.CONNECTION_FAIL);
            }
            String retcode = crh.getParameter("retcode"); // 获取id验证返回状态码，0表示此通知id是财付通发起
            String trade_state = params.get("trade_state"); // 支付状态
            String trade_mode = params.get("trade_mode"); // 交易模式，1即时到账，2中介担保

            // 判断签名及结果
            if (crh.isTenpaySign() && "0".equals(retcode)) { // id验证成功;
                if (!"0".equals(trade_state)) { // 支付结果不等于0，支付失败
                    throw new RespondBusinessException("fail", PaymentExceptionCodes.PAYMENT_FAIL);
                }
                if (!"1".equals(trade_mode)) { // 不是即时到账时返回失败结果
                    throw new RespondBusinessException("fail", PaymentExceptionCodes.NOT_INSTANT_TO_ACCOUNT);
                }
                String orderNo = params.get("out_trade_no"); // 商户订单号
                String fee = params.get("total_fee"); // 金额,以分为单位
                BigDecimal amount = new BigDecimal(fee).divide(MathUtil.HUNDRED); // 转换为以元为单位的金额
                String gatewayPaymentNo = params.get("transaction_id");
                Terminal terminal = null;
                return new PaymentResult(gatewayPaymentNo, amount, terminal, orderNo, "success");
            }
            // 错误时，返回结果未签名，记录retcode、retmsg看失败详情。
            throw new RespondBusinessException("fail", PaymentExceptionCodes.SIGN_FAIL);
        } else {
            // 有可能因为网络原因，请求已经处理，但未收到应答。
            throw new RespondBusinessException("fail", PaymentExceptionCodes.CONNECTION_FAIL);
        }
    }

    private String getRequestUrl(Map<String, String> params) {
        sign(params);
        StringBuffer sb = new StringBuffer();
        Set<Entry<String, String>> entrySet = params.entrySet();
        for (Entry<String, String> entry : entrySet) {
            String k = entry.getKey();
            String v = entry.getValue();
            try {
                sb.append(k + "=" + URLEncoder.encode(v, Strings.ENCODING_UTF8) + "&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        // 去掉最后一个&
        String reqPars = sb.substring(0, sb.lastIndexOf("&"));
        return this.gateNofityFeedbackUrl + "?" + reqPars;
    }

    private PaymentResult getShowResult(Map<String, String> params) throws BusinessException {
        validateSign(params);

        String trade_state = params.get("trade_state"); // 支付状态
        String trade_mode = params.get("trade_mode"); // 交易模式，1即时到账，2中介担保
        if ("0".equals(trade_state) && "1".equals(trade_mode)) { // 直接到账支付成功
            String orderNo = params.get("out_trade_no"); // 商户订单号
            String fee = params.get("total_fee"); // 金额,以分为单位
            String gatewayPaymentNo = params.get("transaction_id"); // 支付交易号
            BigDecimal amount = new BigDecimal(fee).divide(MathUtil.HUNDRED);// 转换为以元为单位的金额
            Terminal terminal = null;
            return new PaymentResult(gatewayPaymentNo, amount, terminal, orderNo, null);
        }
        throw new BusinessException(PaymentExceptionCodes.PAYMENT_FAIL);
    }

    private void validateSign(Map<String, String> params) throws BusinessException {
        StringBuffer sb = new StringBuffer();
        Set<Entry<String, String>> entrySet = params.entrySet();
        for (Entry<String, String> entry : entrySet) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (!"sign".equals(k) && StringUtils.isNotBlank(v)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + this.privateKey);
        String sign = EncryptUtil.encryptByMd5(sb.toString()).toLowerCase();
        if (!sign.equals(params.get("sign").toLowerCase())) {
            throw new BusinessException(PaymentExceptionCodes.SIGN_FAIL);
        }
    }

}
