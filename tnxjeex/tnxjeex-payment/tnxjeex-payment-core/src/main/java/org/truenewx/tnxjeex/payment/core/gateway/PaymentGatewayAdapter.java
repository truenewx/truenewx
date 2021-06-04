package org.truenewx.tnxjeex.payment.core.gateway;

import java.math.BigDecimal;
import java.util.Map;

import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjeex.payment.core.PaymentDefinition;
import org.truenewx.tnxjeex.payment.core.PaymentRequestParameter;
import org.truenewx.tnxjeex.payment.core.PaymentResult;

/**
 * 支付网关适配器
 *
 * @author jianglei
 */
public interface PaymentGatewayAdapter extends PaymentGateway {

    /**
     * 获取向支付网关发起支付请求所需的参数集
     *
     * @param definition 支付定义
     * @return 支付请求参数集
     */
    PaymentRequestParameter getRequestParameter(PaymentDefinition definition);

    /**
     * 获取支付结果
     *
     * @param confirmed 是否确认的通知，false-表示结果展示通知
     * @param terminal  支付终端
     * @param params    结果参数集
     * @return 支付结果
     */
    PaymentResult getResult(boolean confirmed, Terminal terminal, Map<String, String> params);

    /**
     * 发起退款请求
     *
     * @param gatewayPaymentNo 支付网关支付流水号
     * @param paymentAmount    支付金额
     * @param refundNo         退款单编号
     * @param refundAmount     退款金额
     * @return 支付网关退款流水号，返回null说明未成功申请退款
     */
    String requestRefund(String gatewayPaymentNo, BigDecimal paymentAmount, String refundNo, String refundAmount);

}
