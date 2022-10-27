package org.truenewx.tnxjee.core.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.core.util.concurrent.DefaultProgressTask;

/**
 * 资源下载器
 */
@Component
public class ResourceDownloader {

    @Autowired
    private ResourceDownloadProgressTaskExecutor taskExecutor;
    private long interval;

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String download(String url, Map<String, Object> params, File localFile) {
        return this.taskExecutor.submit(new DefaultProgressTask<>(new ResourceDownloadTaskProgress(localFile)) {
            @Override
            protected void execute(ResourceDownloadTaskProgress progress) {
                NetUtil.download(url, params, localFile, (in, length) -> {
                    progress.setTotal(length);

                    int count;
                    byte[] buffer = IOUtils.byteArray(IOUtil.DEFAULT_BUFFER_SIZE);
                    OutputStream out = null;
                    try {
                        out = new FileOutputStream(localFile);

                        while (IOUtils.EOF != (count = in.read(buffer))) {
                            if (progress.isStopped()) {
                                break;
                            }
                            out.write(buffer, 0, count);
                            progress.addCount(count);
                            if (ResourceDownloader.this.interval > 0) {
                                try {
                                    Thread.sleep(ResourceDownloader.this.interval);
                                } catch (InterruptedException ignored) {
                                }
                            }
                        }
                    } catch (IOException e) {
                        LogUtil.error(getClass(), e);
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            LogUtil.error(getClass(), e);
                        }
                    }
                });
                if (progress.isStopped()) { // 如果是中止的任务，则删除本地文件
                    localFile.delete();
                }
            }
        });
    }

    public ResourceDownloadTaskProgress getProgress(String progressId) {
        return this.taskExecutor.getProgress(progressId);
    }

    public void stop(String progressId) {
        ResourceDownloadTaskProgress progress = getProgress(progressId);
        if (progress != null) {
            progress.toStop();
        }
    }

    public void removeProgress(String progressId) {
        this.taskExecutor.remove(progressId);
    }

}
