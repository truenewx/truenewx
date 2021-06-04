package org.truenewx.tnxjeex.payment.core.gateway.alipay;

import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjee.model.spec.enums.Program;
import org.truenewx.tnxjeex.payment.core.PaymentDefinition;
import org.truenewx.tnxjeex.payment.core.PaymentRequestParameter;
import org.truenewx.tnxjeex.payment.core.PaymentResult;
import org.truenewx.tnxjeex.payment.core.gateway.AbstractPaymentGateway;
import org.truenewx.tnxjeex.payment.core.gateway.PaymentChannel;

/**
 * 支付网关：支付宝
 *
 * @author jianglei
 */
public abstract class AlipayPaymentGateway extends AbstractPaymentGateway {

    public String partner;

    public void setPartner(String partner) {
        this.partner = partner;
    }

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.ALIPAY;
    }

    @Override
    public PaymentRequestParameter getRequestParameter(PaymentDefinition definition) {
        SortedMap<String, String> params = new TreeMap<>();
        params.put("service", "create_direct_pay_by_user");
        params.put("partner", this.partner);
        params.put("seller_id", this.partner);
        params.put("_input_charset", Strings.ENCODING_UTF8.toLowerCase());
        params.put("payment_type", "1");
        params.put("notify_url", getResultConfirmUrl());
        if (definition.getTerminal().getProgram() == Program.WEB) { // 网页才需要提供结果展示页URL
            params.put("return_url", getResultShowUrl());
        }
        params.put("out_trade_no", definition.getOrderNo());
        params.put("total_fee", definition.getAmount().toString());
        // String body =
        // this.messageSource.getMessage("info.payment.body",
        // new String[] { description }, Locale.getDefault());
        params.put("subject", definition.getDescription());
        params.put("body", definition.getDescription());

        sign(params);
        return new PaymentRequestParameter(params);
    }

    protected abstract void sign(SortedMap<String, String> params);

    @Override
    public PaymentResult getResult(boolean confirmed, Terminal terminal, Map<String, String> params) {
        validateSign(params);
        String paymentStatus = params.get("trade_status"); // 支付状态
        if ("TRADE_SUCCESS".equals(paymentStatus) || "TRADE_FINISHED".equals(paymentStatus)) { // 支付结果不等于0，支付失败
            String gatewayPaymentNo = params.get("trade_no"); // 支付交易号
            String fee = params.get("total_fee"); // 金额，以分为单位
            BigDecimal amount = new BigDecimal(fee).divide(MathUtil.HUNDRED); // 转换为以元为单位的金额
            String orderNo = params.get("out_trade_no"); // 商户订单号
            return new PaymentResult(gatewayPaymentNo, amount, terminal, orderNo, "success");
        }
        return null; // 状态不为成功，则一律返回null
    }

    protected abstract void validateSign(Map<String, String> params);

}
