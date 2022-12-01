package org.truenewx.tnxjeex.fss.service;

import java.util.Map;

import javax.annotation.Nullable;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjeex.fss.model.FssUploadLimit;
import org.truenewx.tnxjeex.fss.service.storage.FssStorageProvider;

/**
 * 文件存储服务的服务策略
 *
 * @author jianglei
 */
public interface FssServiceStrategy<I extends UserIdentity<?>> extends FssDirDeletePredicate {

    /**
     * MD5占位符
     */
    String PLACEHOLDER_MD5 = "${md5}";

    /**
     * 获取业务类型，要求在同一个系统中唯一
     *
     * @return 业务类型
     */
    String getType();

    FssStorageProvider getProvider();

    /**
     * 获取在当前策略下，指定用户上传文件的限制条件
     *
     * @param userIdentity 用户标识
     * @return 指定用户上传文件的限制条件
     */
    FssUploadLimit getUploadLimit(I userIdentity);

    /**
     * 获取当前业务相对于整个文件存储服务目录的存储根目录，必须以/开头
     *
     * @return 当前业务相对于整个文件存储服务目录的存储根目录
     */
    default String getStorageRootDir() {
        return Strings.SLASH + getType();
    }

    /**
     * 获取指定用户在指定业务范围下的文件相对于业务存储根目录的相对目录，不包含最后一级的文件名
     *
     * @param userIdentity 用户标识。登录用户才能写文件，所以此处一定不为null
     * @param scope        业务范围
     * @return 相对于业务存储根目录的相对目录，返回null表示没有写权限
     */
    String getStorageRelativeDir(I userIdentity, @Nullable String scope);

    /**
     * 获取指定文件存储时的最后一级文件名，不含扩展名，可以包含PLACEHOLDER_MD5占位表示由框架生成基于内容的MD5编码替代，默认为纯MD5编码
     *
     * @param userIdentity     用户标识。登录用户才能写文件，所以此处一定不为null，且已通过写入权限校验
     * @param scope            业务范围
     * @param originalFilename 原始文件名，不含扩展名
     * @return 指定文件存储时的最后一级文件名
     */
    default String getStorageFilename(I userIdentity, String scope, String originalFilename) {
        return PLACEHOLDER_MD5;
    }

    /**
     * 根据存储路径获取定位路径，定位路径不包含业务类型，默认为存储路径去掉根目录的部分
     *
     * @param storagePath 存储路径
     * @return 文件定位路径
     */
    default String getLocationPath(String storagePath) {
        return storagePath.substring(getStorageRootDir().length());
    }

    /**
     * 根据定位路径获取存储路径，默认形如：/[存储根目录]/[定位路径]
     *
     * @param locationPath 定位路径
     * @return 存储路径
     */
    default String getStoragePath(String locationPath) {
        return getStorageRootDir() + NetUtil.standardizeUrl(locationPath);
    }

    /**
     * 获取指定文件下载时的最后一级文件名，包含扩展名，返回null则由框架采用上传时的原始文件名
     *
     * @param userIdentity     用户标识
     * @param locationDir      定位目录
     * @param locationFilename 定位文件名
     * @return 指定文件下载时的最后一级文件名
     */
    default String getDownloadFilename(I userIdentity, String locationDir, String locationFilename) {
        return null;
    }

    /**
     * 指定文件的成功写完之后触发的处理方法，默认什么都不做
     *
     * @param userIdentity 用户标识。登录用户才能写文件，所以此处一定不为null
     * @param locationPath 定位路径
     */
    default void onWritten(I userIdentity, String locationPath) {
    }

    /**
     * @return 是否公开匿名可读
     */
    default boolean isPublicReadable() {
        return false;
    }

    /**
     * 判断指定用户可否读取指定文件
     *
     * @param userIdentity     用户标识
     * @param locationDir      定位目录
     * @param locationFilename 定位文件名
     * @return 指定用户可否读取指定文件
     */
    default boolean isReadable(I userIdentity, String locationDir, String locationFilename) {
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
     * 判断指定用户可否写入指定文件
     *
     * @param userIdentity     用户标识
     * @param locationDir      定位目录
     * @param locationFilename 定位文件名
     * @return 指定用户可否写入指定文件
     */
    default boolean isWriteable(I userIdentity, String locationDir, String locationFilename) {
        return false;
    }

    /**
     * 判断指定用户可否删除指定文件
     *
     * @param userIdentity     用户标识
     * @param locationDir      定位目录
     * @param locationFilename 定位文件名
     * @return 指定用户可否删除指定文件
     */
    default boolean isDeletable(I userIdentity, String locationDir, String locationFilename) {
        // 删除权限默认与写入权限相同
        return isWriteable(userIdentity, locationDir, locationFilename);
    }

}
