package org.truenewx.tnxjeex.fss.service.model;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjeex.fss.model.FssFileMeta;

/**
 * 文件存储服务的存储路径
 *
 * @author jianglei
 */
public class FssStoragePath {

    // 格式形如：fss://[type]/[dir]/[filename]

    private static final String PROTOCOL = FssFileMeta.STORAGE_URL_PROTOCOL;

    private String type;
    private String dir;
    private String filename;

    public FssStoragePath(String type, String dir, String filename) {
        this.type = type;
        this.dir = dir;
        this.filename = filename;
    }

    public static FssStoragePath of(String s) {
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
                    String type = s.substring(1, index); // 去掉第一级的存储类型
                    String path = s.substring(index);
                    index = path.lastIndexOf(Strings.SLASH); // 最后一个斜杠的位置
                    if (index >= 0) {
                        String filename = path.substring(index + 1); // 拆分出最后一级的文件名
                        String relativeDir = path.substring(0, index); // 去掉文件名后剩余的为相对目录
                        FssStoragePath instance = new FssStoragePath(type, relativeDir, filename);
                        if (instance.isValid()) {
                            return instance;
                        }
                    }
                }
            }
        }
        return null;
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
        String dir = Strings.SLASH.equals(this.dir) ? Strings.EMPTY : this.dir;
        return dir + Strings.SLASH + this.filename;
    }

    public boolean isValid() {
        // 相对目录允许为空，此时存储文件位于上下文根目录下，没有划分子目录
        return StringUtils.isNotBlank(this.type) && StringUtils.isNotBlank(this.filename);
    }

    public String getUrl() {
        return isValid() ? (PROTOCOL.substring(0, PROTOCOL.length() - 1) + toString()) : null;
    }

    @Override
    public String toString() {
        return Strings.SLASH + this.type + getPath();
    }

}
