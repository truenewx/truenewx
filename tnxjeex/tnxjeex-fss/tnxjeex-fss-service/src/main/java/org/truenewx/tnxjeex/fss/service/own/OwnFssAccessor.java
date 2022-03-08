package org.truenewx.tnxjeex.fss.service.own;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjeex.fss.model.FssFileDetail;
import org.truenewx.tnxjeex.fss.service.FssAccessor;
import org.truenewx.tnxjeex.fss.service.model.FssProvider;
import org.truenewx.tnxjeex.fss.service.util.FssUtil;

/**
 * 自有文件存储服务访问器
 *
 * @author jianglei
 */
public class OwnFssAccessor implements FssAccessor {

    private File root;
    private OwnFssFileStreamProvider fileStreamProvider;

    public OwnFssAccessor(String root, OwnFssFileStreamProvider fileStreamProvider) {
        File file = new File(root);
        if (!file.exists()) { // 目录不存在则创建
            file.mkdirs();
        } else { // 必须是个目录
            Assert.isTrue(file.isDirectory(), "root must be a directory");
        }
        Assert.isTrue(file.canRead() && file.canWrite(), "root can not read or write");
        this.root = file;
        this.fileStreamProvider = fileStreamProvider;
    }

    @Override
    public FssProvider getProvider() {
        return FssProvider.OWN;
    }

    @Override
    public void write(InputStream in, String path, String filename) throws IOException {
        // 先上传内容到一个新建的临时文件中，以免在处理过程中原文件被读取
        File tempFile = createTempFile(path);
        OutputStream out = this.fileStreamProvider.getWriteStream(path, tempFile, filename);
        IOUtils.copy(in, out);
        out.close();

        // 然后删除原文件，修改临时文件名为原文件名
        File file = getStorageFile(path);
        if (file.exists()) {
            file.delete();
        }
        tempFile.renameTo(file);
    }

    private File createTempFile(String path) throws IOException {
        // 形如：${正式文件名}_${32位UUID}.temp;
        String relativePath = NetUtil.standardizeUrl(path) + Strings.UNDERLINE + StringUtil.uuid32()
                + Strings.DOT + "temp";
        File file = new File(this.root, relativePath);
        ensureDirs(file);
        file.createNewFile(); // 创建新文件以写入内容
        file.setWritable(true);
        return file;
    }

    /**
     * 确保指定文件的所属目录存在
     *
     * @param file 文件
     */
    private void ensureDirs(File file) {
        File parent = file.getParentFile();
        // 上级目录路径中可能已经存在一个同名文件，导致目录无法创建，此时修改该文件的名称
        while (parent != null) {
            if (parent.exists() && !parent.isDirectory()) {
                parent.renameTo(new File(parent.getAbsolutePath() + ".temp"));
                break;
            }
            parent = parent.getParentFile();
        }
        file.getParentFile().mkdirs(); // 确保目录存在
    }

    private File getStorageFile(String path) {
        File file = new File(this.root, NetUtil.standardizeUrl(path));
        ensureDirs(file);
        return file;
    }

    @Override
    public FssFileDetail getDetail(String path) throws IOException {
        File file = getStorageFile(path);
        if (file.exists()) {
            String filename = this.fileStreamProvider.getOriginalFilename(file);
            if (filename == null) {
                filename = file.getName();
            }
            return new FssFileDetail(filename, file.lastModified(), file.length());
        }
        return null;
    }

    @Override
    public Charset getCharset(String path) {
        File file = getStorageFile(path);
        return FssUtil.getCharset(file);
    }

    @Override
    public InputStream getReadStream(String path) throws IOException {
        File file = getStorageFile(path);
        if (file.exists()) {
            return this.fileStreamProvider.getReadStream(file);
        }
        return null;
    }

    @Override
    public void delete(String path) {
        File file = getStorageFile(path);
        if (file.exists()) {
            file.delete();
        }
        // 删除上级空目录
        File parent = file.getParentFile();
        try {
            while (parent != null && !Files.isSameFile(parent.toPath(), this.root.toPath())) {
                if (IOUtil.isEmptyDictionary(parent)) {
                    parent.delete();
                    parent = parent.getParentFile();
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

    @Override
    public void copy(String sourcePath, String targetPath) {
        File sourceFile = getStorageFile(sourcePath);
        File targetFile = getStorageFile(targetPath);
        IOUtil.copyFile(sourceFile, targetFile);
    }

}
