package org.truenewx.tnxjee.core.io;

import java.io.*;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.HttpClientUtil;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.ThreadUtil;
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

    private final Logger logger = LogUtil.getLogger(getClass());

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String download(String url, Map<String, Object> params, Map<String, String> headers, File targetFile,
            Consumer<ResourceDownloadTaskProgress> endedConsumer) {
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
                LogUtil.error(this.logger, e);
            }
        }
        return this.taskExecutor.submit(new DefaultProgressTask<>(new ResourceDownloadTaskProgress(url)) {
            @Override
            protected void execute(ResourceDownloadTaskProgress progress) {
                File downloadingFile = new File(targetFile.getParentFile(),
                        targetFile.getName() + DOWNLOADING_FILE_EXTENSION);
                try {
                    IOUtil.createFile(downloadingFile);
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
                                ThreadUtil.sleep(ResourceDownloader.this.interval);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (IOException e) {
                    progress.fail(new RuntimeException(e));
                    progress.toStop();
                } catch (RuntimeException e) {
                    progress.fail(e);
                    progress.toStop();
                }
                if (progress.isStopped()) { // 如果是中止的任务，则删除本地文件
                    downloadingFile.delete();
                } else {  // 下载完成后，下载中文件更名为目标文件
                    targetFile.delete();
                    downloadingFile.renameTo(targetFile);
                    ResourceDownloader.this.logger.debug("====== {} is downloaded.", targetFile.getAbsolutePath());
                }
            }

            @Override
            protected void onEnded() {
                if (endedConsumer != null) {
                    endedConsumer.accept(this.progress);
                }
            }
        });
    }

    public ResourceDownloadTaskProgress getProgress(String progressId) {
        return this.taskExecutor.getProgress(progressId);
    }

    public boolean stop(String progressId) {
        ResourceDownloadTaskProgress progress = getProgress(progressId);
        if (progress != null && !progress.isEnded()) {
            progress.toStop();
            return true;
        }
        return false;
    }

    public void removeProgress(String progressId) {
        this.taskExecutor.remove(progressId);
    }

}
