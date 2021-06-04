package org.truenewx.tnxjee.service.impl.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 多部分表单工具类
 *
 * @author jianglei
 */
public class MultipartFormUtil {

    private MultipartFormUtil() {
    }

    public static FileItem createFileItem(String fieldName, InputStream in, String fileName) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem(fieldName, MediaType.MULTIPART_FORM_DATA_VALUE, true, fileName);
        try {
            IOUtils.copy(in, item.getOutputStream());
        } catch (IOException e) {
            LogUtil.error(MultipartFormUtil.class, e);
        }
        return item;
    }

    public static MultipartFile createMultipartFile(String fieldName, InputStream in, String fileName) {
        FileItem fileItem = createFileItem(fieldName, in, fileName);
        return new CommonsMultipartFile(fileItem);
    }

}
