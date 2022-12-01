package org.truenewx.tnxjeex.fss.service.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.truenewx.tnxjee.core.util.function.TrPredicate;
import org.truenewx.tnxjeex.fss.model.FssFileDetail;
import org.truenewx.tnxjeex.fss.service.FssDirDeletePredicate;

/**
 * 文件存储服务的底层存储访问器
 *
 * @author jianglei
 */
public interface FssStorageAccessor {

    FssStorageProvider getProvider();

    void write(InputStream in, String storagePath, String originalFilename) throws IOException;

    /**
     * 获取指定文件的细节
     *
     * @param storagePath 文件存储路径
     * @return 文件细节，指定文件不存在时返回null
     */
    FssFileDetail getDetail(String storagePath);

    /**
     * 获取指定文件的字符编码
     *
     * @param storagePath 文件存储路径
     * @return 指定文件的字符编码，无法获取则返回null
     */
    Charset getCharset(String storagePath);

    /**
     * 获取指定文件的读取输入流
     *
     * @param storagePath 文件存储路径
     * @return 读取输入流
     * @throws IOException 如果获取过程中出现IO错误
     */
    InputStream getReadStream(String storagePath) throws IOException;

    /**
     * 删除指定存储路径的文件或目录
     *
     * @param storagePath        存储路径
     * @param dirDeletePredicate 所属目录是否需要删除的断言
     */
    void delete(String storagePath, FssDirDeletePredicate dirDeletePredicate);

    void copy(String sourceStoragePath, String targetStoragePath);

    void move(String sourceStoragePath, String targetStoragePath);

    long getTotalSize(String storageDir);

    void loopReadStream(String storageDir, TrPredicate<String, Long, InputStream> predicate);

}
