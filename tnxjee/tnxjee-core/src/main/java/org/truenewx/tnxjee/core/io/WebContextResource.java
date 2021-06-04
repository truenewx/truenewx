package org.truenewx.tnxjee.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * 相对于Web项目根目录的上下文资源
 *
 * @author jianglei
 * 
 */
public class WebContextResource implements ContextResource {

    private FileSystemResource file;
    private String path;

    public WebContextResource(String path) {
        try {
            String contextRoot = new ClassPathResource(Strings.SLASH).getFile().getParentFile()
                    .getParentFile().getAbsolutePath();
            contextRoot = StringUtils.cleanPath(contextRoot);
            this.path = StringUtils.cleanPath(path);
            this.file = new FileSystemResource(contextRoot + Strings.SLASH + this.path);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getPathWithinContext() {
        return this.path;
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return this.file.createRelative(relativePath);
    }

    @Override
    public boolean exists() {
        return this.file.exists();
    }

    @Override
    public String getDescription() {
        return this.file.getDescription();
    }

    @Override
    public File getFile() throws IOException {
        return this.file.getFile();
    }

    @Override
    public String getFilename() {
        return this.file.getFilename();
    }

    @Override
    public URI getURI() throws IOException {
        return this.file.getURI();
    }

    @Override
    public URL getURL() throws IOException {
        return this.file.getURL();
    }

    @Override
    public boolean isOpen() {
        return this.file.isOpen();
    }

    @Override
    public boolean isReadable() {
        return this.file.isReadable();
    }

    @Override
    public long lastModified() throws IOException {
        return this.file.lastModified();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.file.getInputStream();
    }

    @Override
    public long contentLength() throws IOException {
        return this.file.contentLength();
    }

}
