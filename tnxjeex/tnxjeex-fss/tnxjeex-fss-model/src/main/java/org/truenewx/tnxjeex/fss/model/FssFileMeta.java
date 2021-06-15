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
    private Boolean imageable;

    public FssFileMeta(String storageUrl) {
        this.storageUrl = storageUrl;
    }

    public String getId() {
        return EncryptUtil.encryptByMd5(this.storageUrl);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStorageUrl() {
        return this.storageUrl;
    }

    public String getReadUrl() {
        return this.readUrl;
    }

    public void setReadUrl(String readUrl) {
        this.readUrl = readUrl;
    }

    public String getThumbnailReadUrl() {
        return this.thumbnailReadUrl;
    }

    public void setThumbnailReadUrl(String thumbnailReadUrl) {
        this.thumbnailReadUrl = thumbnailReadUrl;
    }

    public Boolean getImageable() {
        return this.imageable;
    }

    public void setImageable(Boolean imageable) {
        this.imageable = imageable;
    }
}
