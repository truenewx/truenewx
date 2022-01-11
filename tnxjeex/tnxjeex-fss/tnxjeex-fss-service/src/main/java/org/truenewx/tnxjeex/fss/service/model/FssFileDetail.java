package org.truenewx.tnxjeex.fss.service.model;

public class FssFileDetail {

    private String filename;
    private long lastModifiedTime;
    private long length;

    public FssFileDetail(String filename, long lastModifiedTime, long length) {
        this.filename = filename;
        this.lastModifiedTime = lastModifiedTime;
        this.length = length;
    }

    public String getFilename() {
        return this.filename;
    }

    public long getLastModifiedTime() {
        return this.lastModifiedTime;
    }

    public long getLength() {
        return this.length;
    }

}
