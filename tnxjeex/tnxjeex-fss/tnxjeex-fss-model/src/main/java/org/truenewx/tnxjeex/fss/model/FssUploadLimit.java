package org.truenewx.tnxjeex.fss.model;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spec.FlatSize;

/**
 * 文件存储服务上传限制
 *
 * @author jianglei
 */
public class FssUploadLimit {

    private int number;
    private long capacity;
    private boolean extensionsRejected;
    private String[] extensions;
    private boolean imageable;
    private Boolean croppable;
    private FlatSize[] sizes;

    public FssUploadLimit(int number, long capacity, boolean extensionsRejected, String... extensions) {
        Assert.isTrue(number >= 0, "number must be not less than 0");
        this.number = number;
        Assert.isTrue(capacity >= 0, "capacity must be not less than 0");
        this.capacity = capacity;
        this.extensionsRejected = extensionsRejected;
        this.extensions = extensions;
    }

    public FssUploadLimit(int number, long capacity, String... extensions) {
        this(number, capacity, false, extensions);
    }

    public int getNumber() {
        return this.number;
    }

    public long getCapacity() {
        return this.capacity;
    }

    public boolean isExtensionsRejected() {
        return this.extensionsRejected;
    }

    public String[] getExtensions() {
        return this.extensions;
    }

    public boolean isImageable() {
        return this.imageable;
    }

    public Boolean getCroppable() {
        return this.croppable;
    }

    public FlatSize[] getSizes() {
        return this.sizes;
    }

    public FssUploadLimit enableImage(boolean croppable, FlatSize... sizes) {
        this.imageable = true;
        this.croppable = croppable;
        this.sizes = sizes;
        return this;
    }

    /**
     * 从指定文件名中获取扩展名，不含句点，没有扩展名的返回空字符串
     *
     * @param filename 文件名
     * @return 扩展名
     */
    public static String getExtension(String filename) {
        String extension = FilenameUtils.getExtension(filename);
        return extension == null ? Strings.EMPTY : extension;
    }

}
