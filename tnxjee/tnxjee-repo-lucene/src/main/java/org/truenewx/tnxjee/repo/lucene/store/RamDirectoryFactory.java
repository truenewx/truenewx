package org.truenewx.tnxjee.repo.lucene.store;

import java.io.IOException;

import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

/**
 * 基于内存的存储目录工厂
 *
 * @author jianglei
 */
public class RamDirectoryFactory implements DirectoryFactory {

    @Override
    public Directory getDirectory(Class<?> indexedClass) throws IOException {
        return new ByteBuffersDirectory();
    }

}
