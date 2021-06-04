package org.truenewx.tnxjee.core.io;

/**
 * 文件内容转换器
 *
 * @author jianglei
 * 
 */
public interface FileContentConverter {

    void convert(String locationPattern, String encoding);

}
