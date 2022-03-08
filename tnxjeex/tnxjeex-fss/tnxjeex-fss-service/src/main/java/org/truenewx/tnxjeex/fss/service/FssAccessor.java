package org.truenewx.tnxjeex.fss.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.truenewx.tnxjeex.fss.model.FssFileDetail;
import org.truenewx.tnxjeex.fss.service.model.FssProvider;

/**
 * 非结构化数据访问器
 *
 * @author jianglei
 */
public interface FssAccessor {

    FssProvider getProvider();

    void write(InputStream in, String path, String filename) throws IOException;

    /**
     * 获取指定文件的细节
     *
     * @param path 文件路径
     * @return 文件细节，指定文件不存在时返回null
     */
    FssFileDetail getDetail(String path) throws IOException;

    /**
     * 获取指定文件的字符编码
     *
     * @param path 文件路径
     * @return 指定文件的字符编码，无法获取则返回null
     */
    Charset getCharset(String path);

    /**
     * 获取指定文件的读取输入流
     *
     * @param path 文件路径
     * @return 读取输入流
     */
    InputStream getReadStream(String path) throws IOException;

    /**
     * 删除指定路径的文件或目录
     *
     * @param path               文件或目录路径
     * @param dirDeletePredicate
     */
    void delete(String path, FssDirDeletePredicate dirDeletePredicate);

    void copy(String sourcePath, String targetPath);
}
