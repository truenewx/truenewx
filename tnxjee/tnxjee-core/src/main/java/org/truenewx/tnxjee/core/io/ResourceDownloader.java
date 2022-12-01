package org.truenewx.tnxjee.core.io;

import java.io.*;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.HttpClientUtil;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.concurrent.DefaultProgressTask;

/**
 * 资源下载器
 */
@Component
public class ResourceDownloader {

    private static final String DOWNLOADING_FILE_EXTENSION = ".downloading";

    @Autowired
    private ResourceDownloadProgressTaskExecutor taskExecutor;
    private long interval;

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String download(String url, Map<String, Object> params, Map<String, String> headers, File targetFile) {
        if (targetFile.exists()) { // 目标文件已存在，判断是否正在下载中
            String progressId = ResourceDownloadTaskProgress.generateId(url);
            ResourceDownloadTaskProgress progress = getProgress(progressId);
            if (progress != null) { // 正在下载中则返回进度id
                return progressId;
            }
            // 不是正在下载中，则删除目标文件，重新下载
            try {
                FileUtils.forceDelete(targetFile);
            } catch (IOException e) {
                LogUtil.error(getClass(), e);
            }
        }
        return this.taskExecutor.submit(new DefaultProgressTask<>(new ResourceDownloadTaskProgress(url)) {
            @Override
            protected void execute(ResourceDownloadTaskProgress progress) {
                File downloadingFile = new File(targetFile.getParentFile(),
                        targetFile.getName() + DOWNLOADING_FILE_EXTENSION);
                try {
                    HttpClientUtil.download(url, params, headers, (responseEntity, responseHeaders) -> {
                        progress.setTotal(responseEntity.getContentLength());

                        int count;
                        byte[] buffer = IOUtils.byteArray(IOUtil.DEFAULT_BUFFER_SIZE);
                        try (InputStream in = responseEntity.getContent();
                             OutputStream out = new FileOutputStream(downloadingFile)) {
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
                    downloadingFile.delete();
                } else {  // 下载完成后，下载中文件更名为目标文件
                    targetFile.delete();
                    downloadingFile.renameTo(targetFile);
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
