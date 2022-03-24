package org.truenewx.tnxjeex.fss.api.model;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Fss转储命令
 */
public class FssTransferCommand {

    private String sourceUrl;
    /**
     * 文件扩展名，原文件和目标文件的扩展名一致
     */
    private String extension;
    private String targetType;
    private String targetScope;

    public String getSourceUrl() {
        return this.sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
        this.extension = FilenameUtils.getExtension(sourceUrl);
    }

    public String getExtension() {
        return this.extension;
    }

    /**
     * 设置扩展名，当原地址中不包含扩展名时需要设置扩展名
     *
     * @param extension 扩展名
     */
    public void setExtension(String extension) {
        if (StringUtils.isBlank(this.extension)) {
            this.extension = extension;
        }
    }

    public String getTargetType() {
        return this.targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetScope() {
        return this.targetScope;
    }

    public void setTargetScope(String targetScope) {
        this.targetScope = targetScope;
    }

}
