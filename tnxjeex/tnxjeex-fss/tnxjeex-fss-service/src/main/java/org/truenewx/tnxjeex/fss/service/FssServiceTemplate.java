package org.truenewx.tnxjeex.fss.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.service.Service;
import org.truenewx.tnxjee.service.spec.upload.FileUploadLimit;
import org.truenewx.tnxjeex.fss.model.FssFileMeta;

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
    FileUploadLimit getUploadLimit(String type, I userIdentity);

    /**
     * 指定用户在业务授权类型下写文件
     *
     * @param type         业务类型
     * @param scope        业务模型标识
     * @param userIdentity 用户标识
     * @param fileSize     文件大小
     * @param filename     文件名
     * @param in           输入流
     * @return 写好的文件的存储URL
     * @throws IOException 如果写的过程中出现错误
     */
    String write(String type, String scope, I userIdentity, long fileSize, String filename, InputStream in)
            throws IOException;

    /**
     * 指定用户获取指定内部存储URL对应的外部读取URL
     *
     * @param userIdentity 用户标识
     * @param storageUrl   存储URL
     * @param thumbnail    是否缩略图
     * @return 外部读取URL
     */
    String getReadUrl(I userIdentity, String storageUrl, boolean thumbnail);

    /**
     * 获取指定资源的读取元信息
     *
     * @param userIdentity 用户标识
     * @param storageUrl   存储URL
     * @return 指定资源的读取元信息
     */
    FssFileMeta getMeta(I userIdentity, String storageUrl);

    /**
     * 获取指定文件的最后修改时间
     *
     * @param userIdentity 用户标识
     * @param path         文件路径
     * @return 最后修改时间毫秒数，指定资源不存在时返回null
     */
    Long getLastModifiedTime(I userIdentity, String path);

    /**
     * 指定用户读取指定路径的文件内容到指定输出流中
     *
     * @param userIdentity 用户标识
     * @param path         文件路径
     * @param out          输出流
     * @throws IOException 如果读的过程中出现错误
     */
    void read(I userIdentity, String path, OutputStream out) throws IOException;

}
