package org.truenewx.tnxjeex.fss.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;

/**
 * 文件存储服务的文件定位标识，可唯一标识一个业务文件，其字符串形式类似URL
 *
 * @author jianglei
 */
public class FssFileLocation {

    // 格式形如：fss://[type]/[dir]/[filename]

    private static final String PROTOCOL = FssFileMeta.STORAGE_URL_PROTOCOL;

    private String type;
    private String dir;
    private String filename;

    public FssFileLocation(String type, String dir, String filename) {
        Assert.isTrue(StringUtils.isNotBlank(type), "The 'type' must be not blank");
        Assert.isTrue(StringUtils.isNotBlank(filename), "The 'filename' must be not blank");
        this.type = type;
        // 确保路径格式正确
        if (StringUtils.isBlank(dir)) {
            this.dir = Strings.EMPTY;
        } else {
            this.dir = NetUtil.standardizeUrl(dir);
        }
        this.filename = filename;
    }

    public static FssFileLocation of(String type, String path) {
        if (path != null) {
            String dir = null;
            String filename = path;
            int index = path.lastIndexOf(Strings.SLASH); // 最后一个斜杠的位置
            if (index >= 0) {
                filename = path.substring(index + 1); // 拆分出最后一级的文件名
                dir = path.substring(0, index); // 去掉文件名后剩余的为目录
            } // 如果没有斜杠，说明路径里只有文件名
            return new FssFileLocation(type, dir, filename);
        }
        return null;
    }

    public static FssFileLocation of(String s) {
        if (StringUtils.isNotBlank(s)) {
            s = NetUtil.standardizeUrl(s);
            if (s.startsWith(PROTOCOL)) {
                s = s.substring(PROTOCOL.length() - 1);
            } else if (s.startsWith("//")) {
                s = s.substring(1);
            }
            // 预处理后不以/开头的地址不支持，典型的如：http://
            if (s.startsWith(Strings.SLASH)) {
                int index = s.indexOf(Strings.SLASH, 1); // 第二个斜杠的位置
                if (index > 0) {
                    String type = s.substring(1, index); // 第一级为业务类型
                    String path = s.substring(index);
                    return of(type, path);
                }
            }
        }
        return null;
    }

    public static String toUrl(String type, String path) {
        return PROTOCOL + type + path;
    }

    public String getType() {
        return this.type;
    }

    public String getDir() {
        return this.dir;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getPath() {
        return this.dir + Strings.SLASH + this.filename;
    }

    @Override
    public String toString() {
        return toUrl(this.type, getPath());
    }

}
