package org.truenewx.tnxjeex.fss.service;

import java.util.Map;

import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.service.spec.upload.FileUploadLimit;
import org.truenewx.tnxjeex.fss.service.model.FssProvider;

/**
 * 文件存储服务的访问策略
 *
 * @author jianglei
 */
public interface FssAccessStrategy<I extends UserIdentity<?>> {

    /**
     * 获取业务类型，要求在同一个系统中唯一
     *
     * @return 业务类型
     */
    String getType();

    FssProvider getProvider();

    /**
     * 获取在当前策略下，指定用户上传文件的限制条件
     *
     * @param userIdentity 用户标识
     * @return 指定用户上传文件的限制条件
     */
    FileUploadLimit getUploadLimit(I userIdentity);

    /**
     * 获取存储路径上下文根，要求在同一个系统中唯一。其与存储类型对应，不被包含在存储路径中
     *
     * @return 存储路径上下文根
     */
    String getContextPath();

    /**
     * 获取指定资源的相对于上下文根的存储目录，不包含最后一级的文件名
     *
     * @param scope        业务范围
     * @param userIdentity 用户标识。登录用户才能写资源，所以此处一定不为null
     * @return 相对于上下文根的存储目录，返回null表示没有写权限
     */
    String getRelativeDir(String scope, I userIdentity);

    /**
     * 获取指定资源的最后一级文件名，不含扩展名，返回null表示交由框架生成基于内容的MD5编码文件名
     *
     * @param scope            业务范围
     * @param userIdentity     用户标识。登录用户才能写资源，所以此处一定不为null
     * @param originalFilename 原始文件名，不含扩展名
     * @return 指定资源的最后一级文件名
     */
    default String getFilename(String scope, I userIdentity, String originalFilename) {
        return null;
    }

    /**
     * @return 是否公开匿名可读
     */
    default boolean isPublicReadable() {
        return false;
    }

    /**
     * 判断指定用户对指定相对路径是否可读
     *
     * @param userIdentity 用户标识
     * @param relativeDir  相对目录，不包含文件名
     * @return 指定用户对指定相对目录是否可读
     */
    default boolean isReadable(I userIdentity, String relativeDir) {
        return isPublicReadable();
    }

    /**
     * 获取缩略图读取参数集，仅在文件为图片时有效，返回空时表示不支持缩略图
     *
     * @return 缩略图读取参数集
     */
    default Map<String, String> getThumbnailParameters() {
        return null;
    }

    /**
     * 判断指定用户对指定相对路径是否可删除
     *
     * @param userIdentity 用户标识
     * @param relativeDir  相对目录，不包含文件名
     * @return 指定用户对指定相对路径是否可删除
     */
    default boolean isDeletable(I userIdentity, String relativeDir) {
        return false;
    }

}
