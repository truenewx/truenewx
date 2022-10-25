package org.truenewx.tnxjee.core.io;

import java.io.File;

import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.core.util.concurrent.TaskProgress;

/**
 * 资源下载任务进度
 */
public class ResourceDownloadTaskProgress extends TaskProgress<String> {

    private long total;
    private long count;

    public ResourceDownloadTaskProgress(File file) {
        super(generateId(file));
    }

    public static String generateId(File file) {
        return EncryptUtil.encryptByMd5(file.getAbsolutePath());
    }

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void addCount(long count) {
        this.count += count;
    }

    public long getCount() {
        return this.count;
    }

}
