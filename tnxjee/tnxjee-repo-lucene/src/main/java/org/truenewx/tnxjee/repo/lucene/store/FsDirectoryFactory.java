package org.truenewx.tnxjee.repo.lucene.store;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

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
    public Directory getDirectory(String path) throws IOException {
        File dir = getDir(path);
        return new NIOFSDirectory(dir.toPath());
    }

    private File getDir(String path) {
        return new File(this.root, path);
    }

    @Override
    public long getSpaceSize(String path) {
        File dir = getDir(path);
        if (dir.exists()) {
            return FileUtils.sizeOfDirectory(dir);
        }
        return 0;
    }

}
