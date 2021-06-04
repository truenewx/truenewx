package org.truenewx.tnxjeex.payment.core.gateway.wxpay;

import org.apache.http.client.HttpClient;

/**
 * 微信支付常量
 */
class WXPayConstants {

    public enum SignType {
        MD5, HMACSHA256
    }

    public static String DOMAIN_API = "api.mch.weixin.qq.com";
    public static String DOMAIN_API2 = "api2.mch.weixin.qq.com";
    public static String DOMAIN_APIHK = "apihk.mch.weixin.qq.com";
    public static String DOMAIN_APIUS = "apius.mch.weixin.qq.com";

    public static String FAIL = "FAIL";
    public static String SUCCESS = "SUCCESS";
    public static String HMACSHA256 = "HMAC-SHA256";
    public static String MD5 = "MD5";

    public static String FIELD_SIGN = "sign";
    public static String FIELD_SIGN_TYPE = "sign_type";

    public static String WXPAYSDK_VERSION = "WXPaySDK/3.0.9";
    public static String USER_AGENT = WXPAYSDK_VERSION + " (" + System.getProperty("os.arch") + " "
            + System.getProperty("os.name") + " " + System.getProperty("os.version") + ") Java/"
            + System.getProperty("java.version") + " HttpClient/"
            + HttpClient.class.getPackage().getImplementationVersion();

    public static String MICROPAY_URL_SUFFIX = "/pay/micropay";
    public static String UNIFIEDORDER_URL_SUFFIX = "/pay/unifiedorder";
    public static String ORDERQUERY_URL_SUFFIX = "/pay/orderquery";
    public static String REVERSE_URL_SUFFIX = "/secapi/pay/reverse";
    public static String CLOSEORDER_URL_SUFFIX = "/pay/closeorder";
    public static String REFUND_URL_SUFFIX = "/secapi/pay/refund";
    public static String REFUNDQUERY_URL_SUFFIX = "/pay/refundquery";
    public static String DOWNLOADBILL_URL_SUFFIX = "/pay/downloadbill";
    public static String REPORT_URL_SUFFIX = "/payitil/report";
    public static String SHORTURL_URL_SUFFIX = "/tools/shorturl";
    public static String AUTHCODETOOPENID_URL_SUFFIX = "/tools/authcodetoopenid";

    // sandbox
    public static String SANDBOX_MICROPAY_URL_SUFFIX = "/sandboxnew/pay/micropay";
    public static String SANDBOX_UNIFIEDORDER_URL_SUFFIX = "/sandboxnew/pay/unifiedorder";
    public static String SANDBOX_ORDERQUERY_URL_SUFFIX = "/sandboxnew/pay/orderquery";
    public static String SANDBOX_REVERSE_URL_SUFFIX = "/sandboxnew/secapi/pay/reverse";
    public static String SANDBOX_CLOSEORDER_URL_SUFFIX = "/sandboxnew/pay/closeorder";
    public static String SANDBOX_REFUND_URL_SUFFIX = "/sandboxnew/secapi/pay/refund";
    public static String SANDBOX_REFUNDQUERY_URL_SUFFIX = "/sandboxnew/pay/refundquery";
    public static String SANDBOX_DOWNLOADBILL_URL_SUFFIX = "/sandboxnew/pay/downloadbill";
    public static String SANDBOX_REPORT_URL_SUFFIX = "/sandboxnew/payitil/report";
    public static String SANDBOX_SHORTURL_URL_SUFFIX = "/sandboxnew/tools/shorturl";
    public static String SANDBOX_AUTHCODETOOPENID_URL_SUFFIX = "/sandboxnew/tools/authcodetoopenid";

}
