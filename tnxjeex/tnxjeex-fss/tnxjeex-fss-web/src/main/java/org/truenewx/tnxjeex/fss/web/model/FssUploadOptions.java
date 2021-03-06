package org.truenewx.tnxjeex.fss.web.model;

import org.truenewx.tnxjee.core.spec.FlatSize;
import org.truenewx.tnxjeex.fss.model.FssUploadLimit;

/**
 * 文件存储服务上传配置
 */
public class FssUploadOptions {

    private FssUploadLimit limit;
    private boolean publicReadable;

    public FssUploadOptions(FssUploadLimit limit, boolean publicReadable) {
        this.limit = limit;
        this.publicReadable = publicReadable;
    }

    public int getNumber() {
        return this.limit.getNumber();
    }

    public long getCapacity() {
        return this.limit.getCapacity();
    }

    public boolean isExtensionsRejected() {
        return this.limit.isExtensionsRejected();
    }

    public String[] getExtensions() {
        return this.limit.getExtensions();
    }

    public boolean isImageable() {
        return this.limit.isImageable();
    }

    public Boolean getCroppable() {
        return this.limit.getCroppable();
    }

    public FlatSize[] getSizes() {
        return this.limit.getSizes();
    }

    public boolean isPublicReadable() {
        return this.publicReadable;
    }
}
