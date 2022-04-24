package org.truenewx.tnxjeex.fss.model;

/**
 * 文件存储服务的文件细节
 */
public class FssFileDetail {

    private String originalFilename;
    private long lastModifiedTime;
    private long length;

    public FssFileDetail(String originalFilename, long lastModifiedTime, long length) {
        this.originalFilename = originalFilename;
        this.lastModifiedTime = lastModifiedTime;
        this.length = length;
    }

    public String getOriginalFilename() {
        return this.originalFilename;
    }

    public long getLastModifiedTime() {
        return this.lastModifiedTime;
    }

    public long getLength() {
        return this.length;
    }

}
