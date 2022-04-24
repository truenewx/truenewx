package org.truenewx.tnxjeex.fss.service.storage.own;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 自有文件存储服务的文件访问流提供者
 */
public interface OwnFssFileStreamProvider {

    /**
     * 获取指定文件的写入输出流
     *
     * @param file             目标文件
     * @param originalFilename 原始文件名
     * @return 写入输出流
     */
    OutputStream getWriteStream(File file, String originalFilename) throws IOException;

    /**
     * 获取指定文件的原始文件名
     *
     * @param file 文件
     * @return 原始文件名，null-指定文件的文件名作为原始文件名
     */
    String getOriginalFilename(File file);

    /**
     * 获取指定文件的读取输入流
     *
     * @param source 来源文件
     * @return 读取输入流
     */
    InputStream getReadStream(File source) throws IOException;

}
