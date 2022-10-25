package org.truenewx.tnxjee.core.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.core.util.concurrent.DefaultProgressTask;
import org.truenewx.tnxjee.core.util.concurrent.ProgressTaskExecutor;

/**
 * 资源下载器
 */
@Component
public class ResourceDownloader {

    private ProgressTaskExecutor<ResourceDownloadTaskProgress, String> taskExecutor;

    public ResourceDownloader(ExecutorService executorService) {
        this.taskExecutor = new ProgressTaskExecutor<>(executorService);
    }

    public String download(String url, Map<String, Object> params, File localFile) {
        ResourceDownloadTaskProgress progress = new ResourceDownloadTaskProgress(localFile);
        return this.taskExecutor.submit(new DefaultProgressTask<>(progress) {
            @Override
            public void run() {
                NetUtil.download(url, params, localFile, (in, length) -> {
                    this.progress.setTotal(length);
                    this.progress.start();

                    int count;
                    byte[] buffer = IOUtils.byteArray(IOUtil.DEFAULT_BUFFER_SIZE);
                    OutputStream out = null;
                    try {
                        out = new FileOutputStream(localFile);

                        while (IOUtils.EOF != (count = in.read(buffer))) {
                            out.write(buffer, 0, count);
                            this.progress.addCount(count);
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
                    
                    this.progress.complete();
                });
            }
        });
    }

    public ResourceDownloadTaskProgress getProgress(String progressId) {
        return this.taskExecutor.getProgress(progressId);
    }

}
