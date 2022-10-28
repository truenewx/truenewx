package org.truenewx.tnxjee.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * 锁定中文件
 *
 * @author jianglei
 */
public class LockingFile implements AutoCloseable {

    private File file;
    private RandomAccessFile accessor;

    public LockingFile(File file) throws FileNotFoundException {
        this.file = file;
        this.accessor = new RandomAccessFile(file, "rw");
    }

    @Override
    public void close() throws Exception {
        this.accessor.close();
        this.file.delete();
    }

}
