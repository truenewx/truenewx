package org.truenewx.tnxjee.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.core.Strings;

/**
 *
 */
public class Mimetypes {

    public static final String DEFAULT_MIMETYPE = "application/octet-stream";
    private static Mimetypes INSTANCE = null;

    private Properties properties = new Properties();

    private Mimetypes() {
    }

    public synchronized static Mimetypes getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        INSTANCE = new Mimetypes();
        Resource resource = new ClassPathResource("META-INF/mime-types.properties");
        InputStream in = null;
        try {
            in = resource.getInputStream();
            INSTANCE.loadMimetypes(in);
        } catch (IOException e) {
            LogUtil.error(Mimetypes.class, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                LogUtil.error(Mimetypes.class, ex);
            }
        }
        return INSTANCE;
    }

    public void loadMimetypes(InputStream in) throws IOException {
        this.properties.load(in);
    }

    private String getMimetypeByExtension(String fileName) {
        String extension = fileName;
        int index = fileName.lastIndexOf(Strings.DOT);
        if (0 <= index && index + 1 < fileName.length()) {
            extension = fileName.substring(index + 1);
        }
        extension = extension.toLowerCase();
        return this.properties.getProperty(extension);
    }

    public String getMimetype(String fileName) {
        String mimeType = getMimetypeByExtension(fileName);
        return mimeType == null ? DEFAULT_MIMETYPE : mimeType;
    }

    public String getMimetype(File file) {
        return getMimetype(file.getName());
    }
}
