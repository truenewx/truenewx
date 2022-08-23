package org.truenewx.tnxjeex.payment.model;

import org.truenewx.tnxjee.core.caption.Caption;

/**
 * 支付请求方式
 */
public enum PaymentRequestMode {

    @Caption("二维码")
    QRCODE,

    @Caption("以GET方式打开链接")
    GET,

    @Caption("以POST方式提交参数")
    POST,

}
