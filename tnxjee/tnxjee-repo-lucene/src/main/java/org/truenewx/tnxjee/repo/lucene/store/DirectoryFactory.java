package org.truenewx.tnxjee.repo.lucene.store;

import java.io.IOException;

import org.apache.lucene.store.Directory;

/**
 * 存储目录工厂
 *
 * @author jianglei
 */
public interface DirectoryFactory {

    Directory getDirectory(String path) throws IOException;

    /**
     * @param path 目录相对路径
     * @return 占用空间大小
     */
    long getSpaceSize(String path);

}
