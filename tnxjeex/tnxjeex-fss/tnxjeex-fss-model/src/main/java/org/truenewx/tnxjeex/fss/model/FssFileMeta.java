package org.truenewx.tnxjeex.fss.model;

import org.truenewx.tnxjee.core.spec.FlatSize;
import org.truenewx.tnxjee.core.util.EncryptUtil;

/**
 * 文件存储服务的文件元数据
 *
 * @author jianglei
 */
public class FssFileMeta {

    private String name;
    private String locationUrl;
    private String readUrl;
    private String thumbnailReadUrl;
    private String downloadUrl;
    private Boolean imageable;
    private FlatSize size;

    protected FssFileMeta() {
    }

    public FssFileMeta(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public String getId() {
        return EncryptUtil.encryptByMd5(this.locationUrl);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationUrl() {
        return this.locationUrl;
    }

    protected void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
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

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Boolean getImageable() {
        return this.imageable;
    }

    public void setImageable(Boolean imageable) {
        this.imageable = imageable;
    }

    public FlatSize getSize() {
        return this.size;
    }

    public void setSize(FlatSize size) {
        this.size = size;
    }

}
