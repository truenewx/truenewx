package org.truenewx.tnxjeex.notice.service.sms.content;

import java.util.Locale;
import java.util.Map;

/**
 * 短信内容提供者
 *
 * @author jianglei
 */
public interface SmsContentProvider {
    /**
     * @return 支持的业务类型集合
     */
    String[] getTypes();

    /**
     * 获取指定区域的签名
     *
     * @param locale 区域
     * @return 签名，不支持签名的返回null
     */
    String getSignName(Locale locale);

    /**
     * 根据指定参数映射集获取短信内容
     *
     * @param params 参数映射集
     * @param locale 区域
     * @return 短信内容
     */
    String getContent(Map<String, Object> params, Locale locale);

    /**
     * @return 允许发送的最大条数，&lt;=0时不限
     */
    int getMaxCount();
}
