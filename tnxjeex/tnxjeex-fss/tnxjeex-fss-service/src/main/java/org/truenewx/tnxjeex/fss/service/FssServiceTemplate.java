package org.truenewx.tnxjeex.fss.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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
     * @param scope        业务范围
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
     * @param storageUrl   文件路径
     * @return 最后修改时间毫秒数，指定资源不存在时返回null
     */
    Long getLastModifiedTime(I userIdentity, String storageUrl);

    /**
     * 指定用户读取指定路径的文件内容到指定输出流中
     *
     * @param userIdentity 用户标识
     * @param storageUrl   文件路径
     * @param out          输出流
     * @throws IOException 如果读的过程中出现错误
     */
    void read(I userIdentity, String storageUrl, OutputStream out) throws IOException;

    /**
     * 读取指定指定路径的文件内容为字符串，字符编码：UTF-8
     *
     * @param userIdentity 用户标识
     * @param path         文件路径
     * @return 字符串内容
     * @throws IOException 如果读的过程中出现错误
     */
    default String read(I userIdentity, String path) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        read(userIdentity, path, out);
        out.close();
        return out.toString(StandardCharsets.UTF_8);
    }

    /**
     * 删除指定文件
     *
     * @param userIdentity 用户标识
     * @param storageUrl   文件存储地址
     */
    void delete(I userIdentity, String storageUrl);

    /**
     * 复制指定文件为新的业务范围所表示的文件，仅在存储文件名由业务范围决定时有效
     *
     * @param userIdentity 用户标识
     * @param storageUrl   原文件存储地址
     * @param newScope     新的业务范围
     * @return 新文件的存储地址
     */
    String copy(I userIdentity, String storageUrl, String newScope);
}
