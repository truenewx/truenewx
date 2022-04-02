package org.truenewx.tnxjeex.fss.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.service.Service;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.fss.model.FssFileDetail;
import org.truenewx.tnxjeex.fss.model.FssFileMeta;
import org.truenewx.tnxjeex.fss.model.FssUploadLimit;

/**
 * 文件存储服务模版
 *
 * @param <I> 用户标识类型
 * @author jianglei
 */
public interface FssServiceTemplate<I extends UserIdentity<?>> extends Service {

    /**
     * 获取指定用户上传指定业务类型的文件上传限制条件
     *
     * @param type         业务类型
     * @param userIdentity 用户标识
     * @return 指定用户上传指定业务类型的文件上传限制条件
     */
    FssUploadLimit getUploadLimit(String type, I userIdentity);

    boolean isPublicReadable(String type);

    /**
     * 指定用户在业务授权类型下写文件
     *
     * @param type             业务类型
     * @param scope            业务范围
     * @param userIdentity     用户标识
     * @param fileSize         文件大小
     * @param originalFilename 原始文件名
     * @param in               输入流
     * @return 写好的文件的存储地址
     * @throws IOException 如果写的过程中出现错误
     */
    String write(String type, String scope, I userIdentity, long fileSize, String originalFilename, InputStream in)
            throws IOException;

    /**
     * 指定用户将文件写入指定存储地址
     *
     * @param storageUrl       存储地址
     * @param userIdentity     用户标识
     * @param fileSize
     * @param originalFilename 原始文件名
     * @param in               输入流
     * @throws IOException 如果写的过程中出现错误
     */
    void write(String storageUrl, I userIdentity, long fileSize, String originalFilename, InputStream in)
            throws IOException;

    /**
     * 指定用户获取指定内部存储URL对应的外部读取URL
     *
     * @param userIdentity 用户标识
     * @param storageUrl   存储地址
     * @param thumbnail    是否缩略图
     * @return 外部读取URL
     */
    String getReadUrl(I userIdentity, String storageUrl, boolean thumbnail);

    /**
     * 判断指定地址是否指定业务类型的外部第三方服务商的读取地址
     *
     * @param type 业务类型
     * @param url  地址
     * @return 指定地址是否指定业务类型的外部第三方服务商的读取地址
     */
    boolean isOutsideReadUrl(String type, String url);

    /**
     * 获取指定资源的读取元信息
     *
     * @param userIdentity 用户标识
     * @param storageUrl   存储地址
     * @return 指定资源的读取元信息
     */
    FssFileMeta getMeta(I userIdentity, String storageUrl);

    /**
     * 获取指定文件的最后修改时间
     *
     * @param userIdentity 用户标识
     * @param storageUrl   存储地址
     * @return 最后修改时间毫秒数，指定资源不存在时返回null
     */
    FssFileDetail getDetail(I userIdentity, String storageUrl);

    /**
     * 获取指定文件的读取输入流
     *
     * @param userIdentity 用户标识
     * @param storageUrl   存储地址
     * @return 读取输入流
     */
    InputStream getReadStream(I userIdentity, String storageUrl);

    /**
     * 指定用户读取指定路径的文件内容到指定输出流中
     *
     * @param userIdentity   用户标识
     * @param storageUrl     存储地址
     * @param out            输出流
     * @param offset         输入流偏移量
     * @param expectedLength 期望读取长度，<0表示读取整个文件
     * @return 实际读取的数据长度
     */
    long read(I userIdentity, String storageUrl, OutputStream out, long offset, long expectedLength);

    /**
     * 读取指定指定路径的文件内容为文本
     *
     * @param userIdentity 用户标识
     * @param storageUrl   存储地址
     * @param limit        文件大小限制，如果>0，则文件大小超出该限制时不读取内容
     * @return 文件内容文本
     * @throws BusinessException 如果指定文件不是文本文件
     */
    String readText(I userIdentity, String storageUrl, long limit);

    /**
     * 删除指定文件
     *
     * @param userIdentity 用户标识
     * @param storageUrl   存储地址
     */
    void delete(I userIdentity, String storageUrl);

    /**
     * 复制指定文件为目标业务范围所表示的文件，仅在存储文件名由业务范围决定时有效
     *
     * @param userIdentity     用户标识
     * @param sourceStorageUrl 原文件存储地址
     * @param targetType       目标业务类型
     * @param targetScope      目标业务范围
     * @return 目标文件的存储地址
     */
    String copy(I userIdentity, String sourceStorageUrl, String targetType, String targetScope);

    /**
     * 移动指定文件为目标业务范围所表示的文件，仅在存储文件名由业务范围决定时有效
     *
     * @param userIdentity     用户标识
     * @param sourceStorageUrl 原文件存储地址
     * @param targetType       目标业务类型
     * @param targetScope      目标业务范围
     * @return 目标文件的存储地址
     */
    default String move(I userIdentity, String sourceStorageUrl, String targetType, String targetScope) {
        String targetStorageUrl = copy(userIdentity, sourceStorageUrl, targetType, targetScope);
        if (!sourceStorageUrl.equals(targetStorageUrl)) {
            delete(userIdentity, sourceStorageUrl);
        }
        return targetStorageUrl;
    }

}
