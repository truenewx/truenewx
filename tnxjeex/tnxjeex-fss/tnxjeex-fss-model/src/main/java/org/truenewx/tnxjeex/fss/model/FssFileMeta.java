package org.truenewx.tnxjeex.fss.model;

import org.truenewx.tnxjee.core.util.EncryptUtil;

/**
 * 文件存储服务的文件元数据
 *
 * @author jianglei
 */
public class FssFileMeta {

    /**
     * 存储路径协议
     */
    public static final String STORAGE_URL_PROTOCOL = "fss://";

    private String name;
    private String storageUrl;
    private String readUrl;
    private String thumbnailReadUrl;

    public FssFileMeta(String name, String storageUrl, String readUrl, String thumbnailReadUrl) {
        this.name = name;
        this.storageUrl = storageUrl;
        this.readUrl = readUrl;
        this.thumbnailReadUrl = thumbnailReadUrl;
    }

    public String getId() {
        return EncryptUtil.encryptByMd5(this.storageUrl);
    }

    public String getName() {
        return this.name;
    }

    public String getStorageUrl() {
        return this.storageUrl;
    }

    public void setReadUrl(String readUrl) {
        this.readUrl = readUrl;
    }

    public String getReadUrl() {
        return this.readUrl;
    }

    public void setThumbnailReadUrl(String thumbnailReadUrl) {
        this.thumbnailReadUrl = thumbnailReadUrl;
    }

    public String getThumbnailReadUrl() {
        return this.thumbnailReadUrl;
    }
}
