package org.truenewx.tnxjee.repo.lucene.store;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.model.annotation.Indexed;

/**
 * 基于文件系统的存储目录工厂
 *
 * @author jianglei
 */
public class FsDirectoryFactory implements DirectoryFactory {

    private File root;

    public FsDirectoryFactory(String root) {
        this.root = new File(root);
    }

    @Override
    public Directory getDirectory(Class<?> indexedClass) throws IOException {
        String path = Strings.EMPTY;
        Indexed indexed = indexedClass.getAnnotation(Indexed.class);
        if (indexed != null) {
            path = indexed.value();
        }
        if (StringUtils.isBlank(path)) {
            path = indexedClass.getSimpleName();
        }
        if (!path.startsWith(Strings.SLASH)) {
            path = Strings.SLASH + path;
        }
        File dir = new File(this.root, path);
        return new NIOFSDirectory(dir.toPath());
    }

}
