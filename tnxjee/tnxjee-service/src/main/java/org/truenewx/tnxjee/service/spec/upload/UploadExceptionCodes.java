package org.truenewx.tnxjee.service.spec.upload;

/**
 * 文件上传业务异常错误码集
 */
public class UploadExceptionCodes {

    private UploadExceptionCodes() {
    }

    /**
     * 容量超限
     */
    public static final String CAPACITY_EXCEEDS_LIMIT = "error.service.upload.capacity_exceeds_limit";

    /**
     * 不支持空扩展名
     */
    public static final String NOT_SUPPORT_BLANK_EXTENSION = "error.service.upload.not_support_blank_extension";

    /**
     * 仅支持指定扩展名
     */
    public static final String ONLY_SUPPORTED_EXTENSION = "error.service.upload.only_supported_extension";

    /**
     * 不支持指定扩展名
     */
    public static final String UNSUPPORTED_EXTENSION = "error.service.upload.unsupported_extension";
}
