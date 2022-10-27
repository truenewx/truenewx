package org.truenewx.tnxjee.core.io;

import java.util.concurrent.ExecutorService;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.concurrent.ProgressTaskExecutor;

/**
 * 资源下载进度任务执行器
 */
@Component
public class ResourceDownloadProgressTaskExecutor extends ProgressTaskExecutor<ResourceDownloadTaskProgress, String> {

    public ResourceDownloadProgressTaskExecutor(ExecutorService executor) {
        super(executor);
    }

}
