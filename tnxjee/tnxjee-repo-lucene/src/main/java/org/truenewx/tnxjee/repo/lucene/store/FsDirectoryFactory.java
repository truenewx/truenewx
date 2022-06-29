package org.truenewx.tnxjee.repo.lucene.store;

import java.io.File;
import java.io.IOException;

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
        File dir = new File(this.root, path);
        return new NIOFSDirectory(dir.toPath());
    }

}
