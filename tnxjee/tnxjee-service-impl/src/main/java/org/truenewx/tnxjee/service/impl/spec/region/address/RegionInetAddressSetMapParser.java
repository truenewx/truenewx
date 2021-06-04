package org.truenewx.tnxjee.service.impl.spec.region.address;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

import org.truenewx.tnxjee.core.spec.InetAddressSet;

/**
 * 区划-网络地址集合映射集解析器
 *
 * @author jianglei
 * 
 */
public interface RegionInetAddressSetMapParser {
    /**
     * 解析指定输入流中的内容为区划-网络地址集合映射集
     *
     * @param in       输入流
     * @param locale   输入流中内容的语言区域
     * @param encoding 字符集
     * @return 区划-网络地址集合映射集
     * @throws IOException 如果从输入流中读取内容时出现错误
     */
    Map<String, InetAddressSet> parse(InputStream in, Locale locale, String encoding)
            throws IOException;
}
