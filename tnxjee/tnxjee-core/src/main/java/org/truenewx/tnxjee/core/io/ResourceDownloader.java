package org.truenewx.tnxjee.core.io;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.IOUtil;
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
        return this.taskExecutor.submit(new DefaultProgressTask<>(new ResourceDownloadTaskProgress(url)) {
            @Override
            protected void execute(ResourceDownloadTaskProgress progress) {
                try {
                    NetUtil.download(url, params, localFile, (length, in, out) -> {
                        progress.setTotal(length);

                        int count;
                        byte[] buffer = IOUtils.byteArray(IOUtil.DEFAULT_BUFFER_SIZE);
                        try {
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
                            throw new RuntimeException(e);
                        }
                    });
                } catch (IOException e) {
                    progress.fail(new RuntimeException(e));
                } catch (RuntimeException e) {
                    progress.fail(e);
                }
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
