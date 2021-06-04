package org.truenewx.tnxjee.core.parser;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * 模板解析器
 *
 * @author jianglei
 * 
 */
public interface TemplateParser {

    /**
     * 用指定参数集解析指定模板内容
     *
     * @param templateContent
     *            模板内容
     * @param params
     *            参数集
     * @param locale
     *            区域
     * @return 解析后的内容
     *
     * @author jianglei
     */
    String parse(String templateContent, Map<String, ?> params, Locale locale);

    /**
     * 用指定参数集解析指定模板文件的内容
     *
     * @param templateFile
     *            模板文件
     * @param params
     *            参数集
     * @param locale
     *            区域
     * @return 解析后的内容
     * @exception 如果读取文件内容出现错误
     *
     * @author jianglei
     */
    String parse(File templateFile, Map<String, ?> params, Locale locale) throws IOException;

}
