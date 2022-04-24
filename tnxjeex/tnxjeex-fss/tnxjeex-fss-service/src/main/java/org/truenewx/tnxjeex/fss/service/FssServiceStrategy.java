package org.truenewx.tnxjeex.fss.service;

import java.util.Map;

import org.springframework.util.Assert;
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
    String getStorageRelativeDir(I userIdentity, String scope);

    /**
     * 获取指定文件存储时的最后一级文件名，不含扩展名，返回null表示交由框架生成基于内容的MD5编码文件名
     *
     * @param userIdentity     用户标识。登录用户才能写文件，所以此处一定不为null，且已通过写入权限校验
     * @param scope            业务范围
     * @param originalFilename 原始文件名
     * @return 指定文件存储时的最后一级文件名
     */
    default String getStorageFilename(I userIdentity, String scope, String originalFilename) {
        return null;
    }

    /**
     * 根据定位目录和定位文件名获取存储路径，默认形如：/[存储根目录]/[定位目录]/[定位文件名]
     *
     * @param locationDir      定位目录
     * @param locationFilename 定位文件名
     * @return 存储路径
     */
    default String getStoragePath(String locationDir, String locationFilename) {
        return getStorageRootDir() + NetUtil.standardizeUrl(locationDir) + Strings.SLASH + locationFilename;
    }

    /**
     * 根据存储目录和存储文件名获取定位路径，默认为：/[存储目录去掉存储根目录后的剩余部分]/[存储文件名]
     *
     * @param storageDir      存储目录
     * @param storageFilename 存储文件名
     * @return 文件定位路径
     */
    default String getLocationPath(String storageDir, String storageFilename) {
        String storageRootDir = getStorageRootDir();
        Assert.isTrue(storageDir.startsWith(storageRootDir + Strings.SLASH),
                "The storageDir must start with '" + storageRootDir + "/'");
        return storageDir.substring(storageRootDir.length()) + Strings.SLASH + storageFilename;
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
     * 判断指定用户可否读取指定相对路径下的指定存储文件
     *
     * @param userIdentity    用户标识
     * @param relativeDir     相对目录
     * @param storageFilename 存储文件名
     * @return 指定用户可否读取指定相对路径下的指定存储文件
     */
    default boolean isReadable(I userIdentity, String relativeDir, String storageFilename) {
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
     * 判断指定用户可否写入指定相对路径下的指定存储文件
     *
     * @param userIdentity    用户标识
     * @param relativeDir     相对目录
     * @param storageFilename 存储文件名
     * @return 指定用户可否写入指定相对路径下的指定存储文件
     */
    default boolean isWriteable(I userIdentity, String relativeDir, String storageFilename) {
        return false;
    }

    /**
     * 判断指定用户可否删除指定相对路径下的指定存储文件
     *
     * @param userIdentity    用户标识
     * @param relativeDir     相对目录
     * @param storageFilename 存储文件名
     * @return 指定用户可否删除指定相对路径下的指定存储文件
     */
    default boolean isDeletable(I userIdentity, String relativeDir, String storageFilename) {
        // 删除权限默认与写入权限相同
        return isWriteable(userIdentity, relativeDir, storageFilename);
    }

}
