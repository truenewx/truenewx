package org.truenewx.tnxjeex.payment.core.gateway.wxpay;

import java.io.InputStream;

interface WxPayConfig {

    /**
     * 获取 App ID
     *
     * @return App ID
     */
    String getAppId();

    /**
     * 获取商户号
     *
     * @return 商户号
     */
    String getMerchantId();

    /**
     * 获取 API 密钥
     *
     * @return API密钥
     */
    String getApiKey();

    /**
     * 获取商户证书（*.p12）内容
     *
     * @return 商户证书内容
     */
    InputStream getCertStream();

    /**
     * @return WXPayDomain, 用于多域名容灾自动切换
     */
    WxPayDomain getWXPayDomain();

    /**
     * @return HTTP(S) 连接超时时间，单位毫秒
     */
    default int getHttpConnectTimeoutMs() {
        return 6 * 1000;
    }

    /**
     * @return HTTP(S) 读数据超时时间，单位毫秒
     */
    default int getHttpReadTimeoutMs() {
        return 8 * 1000;
    }

    /**
     * @return 是否自动上报。 若要关闭自动上报，子类中实现该函数返回 false 即可。
     */
    default boolean shouldAutoReport() {
        return true;
    }

    /**
     * @return 进行健康上报的线程的数量
     */
    default int getReportWorkerNum() {
        return 6;
    }

    /**
     * @return 健康上报缓存消息的最大数量。会有线程去独立上报 粗略计算：加入一条消息200B，10000消息占用空间 2000 KB，约为2MB，可以接受
     */
    default int getReportQueueMaxSize() {
        return 10000;
    }

    /**
     * @return 批量上报，一次最多上报多个数据
     */
    default int getReportBatchSize() {
        return 10;
    }

}
