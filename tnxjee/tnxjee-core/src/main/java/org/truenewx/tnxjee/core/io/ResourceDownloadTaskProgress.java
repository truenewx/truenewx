package org.truenewx.tnxjee.core.io;

import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.core.util.concurrent.TaskProgress;

/**
 * 资源下载任务进度
 */
public class ResourceDownloadTaskProgress extends TaskProgress<String> {

    private String url;
    private long total;
    private long count;
    private RuntimeException exception;

    public ResourceDownloadTaskProgress(String url) {
        super(generateId(url));
        this.url = url;
    }

    public static String generateId(String url) {
        return EncryptUtil.encryptByMd5(url);
    }

    public String getUrl() {
        return this.url;
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

    public void fail(RuntimeException exception) {
        this.exception = exception;
        // 失败时中止后续处理
        toStop();
    }

    public RuntimeException getException() {
        return this.exception;
    }
}
