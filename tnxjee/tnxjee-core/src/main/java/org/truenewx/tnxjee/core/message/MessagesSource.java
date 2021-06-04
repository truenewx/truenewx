package org.truenewx.tnxjee.core.message;

import java.util.Locale;
import java.util.Map;

/**
 * 消息集来源
 *
 * @author jianglei
 * 
 */
public interface MessagesSource {
    /**
     * 获取消息属性映射集
     *
     * @param locale
     *            区域
     *
     * @return 消息属性映射集
     */
    Map<String, String> getMessages(Locale locale);

    /**
     * 获取代码以指定前缀开头的消息属性映射集
     *
     * @param locale
     *            区域
     * @param prefix
     *            消息代码前缀
     * @param resultContainsPrefix
     *            结果集中的key是否包含上述指定前缀
     * @return 消息属性映射集
     */
    Map<String, String> getMessages(Locale locale, String prefix, boolean resultContainsPrefix);
}
