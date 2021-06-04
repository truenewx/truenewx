package org.truenewx.tnxjee.service.spec.upload;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ArrayUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.core.util.Mimetypes;
import org.truenewx.tnxjee.model.spec.FlatSize;
import org.truenewx.tnxjee.service.exception.BusinessException;

/**
 * 文件上传限制
 *
 * @author jianglei
 */
public class FileUploadLimit {

    private int number;
    private long capacity;
    private boolean extensionsRejected;
    private String[] extensions;
    private String[] mimeTypes;
    private boolean imageable;
    private Boolean croppable;
    private FlatSize[] sizes;

    public FileUploadLimit(int number, long capacity, boolean extensionsRejected, String... extensions) {
        Assert.isTrue(number >= 0, "number must be not less than 0");
        this.number = number;
        Assert.isTrue(capacity >= 0, "capacity must be not less than 0");
        this.capacity = capacity;
        this.extensionsRejected = extensionsRejected;
        this.extensions = extensions;
        if (!this.extensionsRejected) {
            this.mimeTypes = new String[extensions.length];
            Mimetypes mimetypes = Mimetypes.getInstance();
            for (int i = 0; i < extensions.length; i++) {
                this.mimeTypes[i] = mimetypes.getMimetype(extensions[i]);
            }
        }
    }

    public FileUploadLimit(int number, long capacity, String... extensions) {
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

    public String[] getMimeTypes() {
        return this.mimeTypes;
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

    public void enableImage(boolean croppable, FlatSize... sizes) {
        this.imageable = true;
        this.croppable = croppable;
        this.sizes = sizes;
    }

    public String validate(long fileSize, String filename) {
        if (fileSize > this.capacity) {
            throw new BusinessException(UploadExceptionCodes.CAPACITY_EXCEEDS_LIMIT,
                    MathUtil.getCapacityCaption(this.capacity, 2));
        }
        String extension = FilenameUtils.getExtension(filename);
        if (StringUtils.isBlank(extension)) {
            throw new BusinessException(UploadExceptionCodes.NOT_SUPPORT_BLANK_EXTENSION);
        }
        if (ArrayUtils.isNotEmpty(this.extensions)) { // 上传限制中没有设置扩展名，则不限定扩展名
            if (this.extensionsRejected) { // 拒绝扩展名模式
                if (ArrayUtil.containsIgnoreCase(this.extensions, extension)) {
                    throw new BusinessException(UploadExceptionCodes.UNSUPPORTED_EXTENSION,
                            StringUtils.join(this.extensions, Strings.COMMA), filename);
                }
            } else { // 允许扩展名模式
                if (!ArrayUtil.containsIgnoreCase(this.extensions, extension)) {
                    throw new BusinessException(UploadExceptionCodes.ONLY_SUPPORTED_EXTENSION,
                            StringUtils.join(this.extensions, Strings.COMMA), filename);
                }
            }
        }
        if (extension.length() > 0) {
            extension = Strings.DOT + extension;
        }
        return extension;
    }

}
