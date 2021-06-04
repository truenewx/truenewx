package org.truenewx.tnxjeex.fss.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
     * 获取指定文件的原始文件名
     *
     * @param path 文件路径
     * @return 原始文件名
     */
    String getOriginalFilename(String path);

    /**
     * 获取指定文件的最后修改时间
     *
     * @param path 存储路径
     * @return 最后修改时间毫秒数，指定文件不存在时返回null
     */
    Long getLastModifiedTime(String path);

    boolean read(String path, OutputStream out) throws IOException;

}
