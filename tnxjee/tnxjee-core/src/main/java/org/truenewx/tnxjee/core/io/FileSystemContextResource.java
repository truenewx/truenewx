package org.truenewx.tnxjee.core.io;

import org.springframework.core.io.ContextResource;
import org.springframework.core.io.FileSystemResource;

/**
 * 基于文件系统的上下文资源
 *
 * @author jianglei
 * 
 */
public class FileSystemContextResource extends FileSystemResource implements ContextResource {

    public FileSystemContextResource(String path) {
        super(path);
    }

    @Override
    public String getPathWithinContext() {
        return getPath();
    }
}
