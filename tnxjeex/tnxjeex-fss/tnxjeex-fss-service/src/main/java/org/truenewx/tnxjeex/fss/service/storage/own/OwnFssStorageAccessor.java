package org.truenewx.tnxjeex.fss.service.storage.own;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.*;
import org.truenewx.tnxjeex.fss.model.FssFileDetail;
import org.truenewx.tnxjeex.fss.service.FssDirDeletePredicate;
import org.truenewx.tnxjeex.fss.service.storage.FssStorageAccessor;
import org.truenewx.tnxjeex.fss.service.storage.FssStorageProvider;
import org.truenewx.tnxjeex.fss.service.util.FssUtil;

/**
 * 自有文件存储服务访问器
 *
 * @author jianglei
 */
public class OwnFssStorageAccessor implements FssStorageAccessor {

    private File root;
    private OwnFssFileStreamProvider fileStreamProvider;
    private ExecutorService executorService;

    public OwnFssStorageAccessor(String root, OwnFssFileStreamProvider fileStreamProvider) {
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

    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public FssStorageProvider getProvider() {
        return FssStorageProvider.OWN;
    }

    @Override
    public void write(InputStream in, String storagePath, String originalFilename) throws IOException {
        // 先上传内容到一个新建的临时文件中，以免在处理过程中原文件被读取
        File tempFile = createTempFile(storagePath);
        OutputStream out = this.fileStreamProvider.getWriteStream(tempFile, originalFilename);
        IOUtils.copy(in, out);
        out.close();

        // 然后删除原文件，修改临时文件名为原文件名
        File file = getStorageFile(storagePath, false);
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

    private File getStorageFile(String path, boolean supportsFilenameWildcard) {
        path = NetUtil.standardizeUrl(path);
        File file = null;
        if (supportsFilenameWildcard) {
            // 如果支持文件名的通配符，则返回找到的第一个匹配的文件，如果找不到匹配的文件，则以普通方式返回一个不存在的文件对象
            int index = path.lastIndexOf(Strings.SLASH);
            String filename = path.substring(index + 1);
            if (filename.contains(Strings.ASTERISK)) {
                File dir = new File(this.root, path.substring(0, index));
                File[] files = dir.listFiles((d, name) -> StringUtil.wildcardMatch(name, filename));
                file = ArrayUtil.get(files, 0);
            }
        }
        if (file == null) {
            file = new File(this.root, path);
            ensureDirs(file);
        }
        return file;
    }

    @Override
    public FssFileDetail getDetail(String storagePath) {
        File file = getStorageFile(storagePath, false);
        if (file.exists()) {
            String originalFilename = this.fileStreamProvider.getOriginalFilename(file);
            if (originalFilename == null) {
                originalFilename = file.getName();
            }
            return new FssFileDetail(originalFilename, file.lastModified(), file.length());
        }
        return null;
    }

    @Override
    public Charset getCharset(String storagePath) {
        File file = getStorageFile(storagePath, false);
        return FssUtil.getCharset(file);
    }

    @Override
    public InputStream getReadStream(String storagePath) throws IOException {
        File file = getStorageFile(storagePath, false);
        if (file.exists()) {
            return this.fileStreamProvider.getReadStream(file);
        }
        return null;
    }

    @Override
    public void delete(String storagePath, FssDirDeletePredicate dirDeletePredicate) {
        File file = getStorageFile(storagePath, true);
        if (file.exists()) {
            file.delete();
        }

        // 删除上级空目录
        this.executorService.submit(() -> {
            File parent = file.getParentFile();
            try {
                while (parent != null && !Files.isSameFile(parent.toPath(), this.root.toPath())) {
                    List<String> subDirs = new ArrayList<>();
                    List<String> filenames = new ArrayList<>();
                    File[] files = parent.listFiles();
                    if (files != null) {
                        for (File f : files) {
                            if (f.isDirectory()) {
                                subDirs.add(f.getName());
                            } else {
                                filenames.add(f.getName());
                            }
                        }
                    }
                    String relativeDir = parent.getAbsolutePath().substring(this.root.getAbsolutePath().length())
                            .replaceAll("\\\\", Strings.SLASH);
                    if (dirDeletePredicate.isDirDeletable(relativeDir, subDirs, filenames)) {
                        parent.delete();
                        parent = parent.getParentFile();
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                LogUtil.error(getClass(), e);
            }
        });
    }

    @Override
    public void copy(String sourceStoragePath, String targetStoragePath) {
        File sourceFile = getStorageFile(sourceStoragePath, false);
        File targetFile = getStorageFile(targetStoragePath, false);
        IOUtil.copyFile(sourceFile, targetFile);
    }

}
