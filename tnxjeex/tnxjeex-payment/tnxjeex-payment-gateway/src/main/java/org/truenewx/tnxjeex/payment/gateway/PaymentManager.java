package org.truenewx.tnxjeex.payment.gateway;

import java.math.BigDecimal;
import java.util.List;

import org.truenewx.tnxjee.core.http.HttpRequestDataProvider;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjeex.payment.model.PaymentDefinition;
import org.truenewx.tnxjeex.payment.model.PaymentRequestParameter;
import org.truenewx.tnxjeex.payment.model.PaymentResult;

/**
 * 支付管理器
 *
 * @author jianglei
 */
public interface PaymentManager {

    /**
     * 获取指定终端类型下可用的支付网关清单
     *
     * @param terminal 终端类型
     * @return 支付网关清单
     */
    List<PaymentGateway> getGateways(Terminal terminal);

    /**
     * 根据支付网关名称获取支付网关
     *
     * @param gatewayName 支付网关名称
     * @return 支付网关
     */
    PaymentGateway getGateway(String gatewayName);

    /**
     * 获取向支付网关发起支付请求所需的参数
     *
     * @param gatewayName 支付网关名称
     * @param definition  支付定义
     * @return 支付请求参数集
     */
    PaymentRequestParameter getRequestParameter(String gatewayName, PaymentDefinition definition);

    /**
     * 通知支付结果
     *
     * @param gatewayName        支付网关名称
     * @param confirmed          是否确认通知，确认通知会触发已支付事件
     * @param notifyDataProvider 通知数据提供者
     * @return 支付结果
     */
    PaymentResult notifyResult(String gatewayName, boolean confirmed, HttpRequestDataProvider notifyDataProvider);

    /**
     * 发起退款请求
     *
     * @param gatewayName      支付网关名称
     * @param gatewayPaymentNo 支付网关支付流水号
     * @param paymentAmount    支付金额
     * @param refundNo         退款单编号
     * @param refundAmount     退款金额
     */
    void requestRefund(String gatewayName, String gatewayPaymentNo, BigDecimal paymentAmount, String refundNo,
            String refundAmount);
}
