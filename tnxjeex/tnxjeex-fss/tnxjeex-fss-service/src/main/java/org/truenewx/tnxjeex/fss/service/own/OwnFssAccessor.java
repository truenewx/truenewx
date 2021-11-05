package org.truenewx.tnxjeex.fss.service.own;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjeex.fss.service.FssAccessor;
import org.truenewx.tnxjeex.fss.service.model.FssProvider;

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
        OutputStream out = this.fileStreamProvider.getOutputStream(path, tempFile, filename);
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
    public String getOriginalFilename(String path) {
        try {
            File file = getStorageFile(path);
            if (file.exists()) {
                String filename = this.fileStreamProvider.getOriginalFilename(path, file);
                // 文件存在则一定返回非null结果
                return filename == null ? file.getName() : filename;
            }
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
        return null;
    }

    @Override
    public Long getLastModifiedTime(String path) {
        File file = getStorageFile(path);
        if (file.exists()) {
            return file.lastModified();
        }
        return null;
    }

    @Override
    public boolean read(String path, OutputStream out) throws IOException {
        File file = getStorageFile(path);
        if (file.exists()) {
            InputStream in = this.fileStreamProvider.getInputStream(file);
            IOUtils.copy(in, out);
            in.close();
            return true;
        }
        // 如果文件不存在，则需要从远程服务器读取内容，并缓存到本地文件
        return false;
    }

    @Override
    public void delete(String path) {
        File file = getStorageFile(path);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void copy(String sourcePath, String targetPath) {
        File sourceFile = getStorageFile(sourcePath);
        File targetFile = getStorageFile(targetPath);
        IOUtil.copyFile(sourceFile, targetFile);
    }

}
