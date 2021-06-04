package org.truenewx.tnxjee.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * IO工具类
 *
 * @author jianglei
 */
public class IOUtil {

    /**
     * 文件路径分隔符
     */
    public static final String FILE_SEPARATOR = System.getProperties()
            .getProperty("file.separator");

    private IOUtil() {
    }

    public static void coverToFile(File file, String data, String encoding) throws IOException {
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        OutputStream out = new FileOutputStream(file);
        out.write(data.getBytes(encoding));
        out.flush();
        out.close();
        out = null;
    }

    /**
     * 以非阻塞方式，读取指定字节输入流中的数据为字符串
     *
     * @param in 字节输入流
     * @return 结果字符串
     * @throws IOException 如果读取过程中出现错误
     */
    public static String readUnblocklyToString(InputStream in) throws IOException {
        return readUnblocklyToString(new BufferedReader(new InputStreamReader(in)));
    }

    /**
     * 以非阻塞方式，以指定字符集读取指定字节输入流中的数据为字符串
     *
     * @param in          字节输入流
     * @param charsetName 字符集
     * @return 结果字符串
     * @throws IOException 如果读取过程中出现错误
     */
    public static String readUnblocklyToString(InputStream in, String charsetName)
            throws IOException {
        return readUnblocklyToString(new BufferedReader(new InputStreamReader(in, charsetName)));
    }

    /**
     * 以非阻塞方式，读取指定字符输入流中的数据为字符串。如果对字符集有要求，请先将字符输入流的字符集设置好
     *
     * @param reader 字符输入流
     * @return 结果字符串
     * @throws IOException 如果读取过程中出现错误
     */
    public static String readUnblocklyToString(Reader reader) throws IOException {
        String s = "";
        char[] c = new char[1024];
        while (reader.ready()) {
            s += new String(c, 0, reader.read(c));
        }
        return s;
    }

    /**
     * 执行指定命令行指令，如果等待毫秒数大于0，则当前线程等待指定毫秒数之后返回，
     *
     * @param command 命令行指令
     */
    public static String executeCommand(String command) {
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            result = IOUtils.toString(process.getInputStream(), Strings.ENCODING_UTF8);
        } catch (IOException | InterruptedException e) {
            LogUtil.error(IOUtil.class, e);
        }
        return result;
    }

    /**
     * 创建指定文件到本地文件系统，如果该文件已存在则不创建
     *
     * @param file 文件
     * @throws IOException 如果创建文件时出现错误
     */
    public static void createFile(File file) throws IOException {
        if (!file.exists()) {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file.createNewFile();
        }
    }

    /**
     * 创建指定目录到本地文件系统，如果该目录已存在则不创建
     *
     * @param dir 目录
     */
    public static void createDirectory(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 替换指定文件指定内容
     *
     * @param filePath    被修改文件路径
     * @param regex       被替换内容
     * @param replacement 替换内容
     */
    public static void replaceFileContent(String filePath, String regex, String replacement) {
        BufferedReader br = null;
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }
        } catch (Exception e) {
            LogUtil.error(IOUtil.class, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    br = null;
                }
            }
        }
        String s = sb.toString().replaceAll(regex, replacement);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(filePath));
            bw.write(s);
        } catch (Exception e) {
            LogUtil.error(IOUtil.class, e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    bw = null;
                }
            }
        }
    }

    /**
     * 查找与指定区域匹配的国际化的资源
     *
     * @param basename  文件基本名
     * @param locale    区域
     * @param extension 文件扩展名
     * @return 与指定区域匹配的国际化的资源，如果找不到则返回null
     */
    public static Resource findI18nResource(String basename, Locale locale, String extension) {
        basename = basename.trim();
        Assert.hasText(basename, "Basename must not be empty");
        basename = basename.replace('\\', '/');
        // 把basename中classpath:替换为classpath*:后进行查找
        StringBuilder searchBasename = new StringBuilder(
                basename.replace(ResourceUtils.CLASSPATH_URL_PREFIX,
                        ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)).append(Strings.ASTERISK);
        if (!extension.startsWith(Strings.DOT)) {
            searchBasename.append(Strings.DOT);
        }
        searchBasename.append(extension);
        // 查找文件资源
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resourcePatternResolver.getResources(searchBasename.toString());
            Resource result = null;
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                String[] fileNameArray = fileName.split(Strings.UNDERLINE);
                if (StringUtils.indexOfIgnoreCase(fileName,
                        locale.getLanguage() + "_" + locale.getCountry(), 0) >= 0) {
                    result = resource;
                    break;
                } else if (StringUtils.indexOfIgnoreCase(fileName, locale.getLanguage(), 0) >= 0) {
                    result = resource;
                } else if (result == null && fileNameArray.length == 1) {
                    result = resource;
                }
            }
            return result;
        } catch (IOException e) {
            LogUtil.error(IOUtil.class, e);
        }
        return null;
    }

    /**
     * 查找与指定目录下区域匹配的国际化的文件
     *
     * @param baseDir   目录
     * @param basename  文件基本名称 aa，不含扩展名
     * @param locale    区域
     * @param extension 文件扩展名，句点包含与否均可
     * @return 找到的文件，如果没找到则返回null
     */
    public static File findI18nFileByDir(String baseDir, String basename, String extension,
            Locale locale) {
        StringBuilder searchFileName = new StringBuilder(basename).append(Strings.ASTERISK);
        if (!extension.startsWith(Strings.DOT)) {
            searchFileName.append(Strings.DOT);
        }
        searchFileName.append(extension);
        List<File> resultList = new ArrayList<>();
        findFiles(baseDir, searchFileName.toString(), resultList);

        File returnFile = null;
        if (resultList.size() > 0) {
            for (File file : resultList) {
                String resultFileName = file.getName();
                String[] fileNameArray = resultFileName.split(Strings.UNDERLINE);
                if (StringUtils.indexOfIgnoreCase(resultFileName,
                        locale.getLanguage() + "_" + locale.getCountry(), 0) >= 0) {
                    returnFile = file;
                    break;
                } else if (StringUtils.indexOfIgnoreCase(resultFileName, locale.getLanguage(),
                        0) >= 0) {
                    returnFile = file;
                } else if (returnFile == null && fileNameArray.length == 1) {
                    returnFile = file;
                }
            }

        }
        return returnFile;
    }

    /**
     * 递归查找文件
     *
     * @param baseDirName    查找的文件夹路径
     * @param targetFileName 需要查找的文件名
     * @param fileList       查找到的文件集合
     */
    public static void findFiles(String baseDirName, String targetFileName, List<File> fileList) {
        File baseDir = new File(baseDirName); // 创建一个File对象
        if (baseDir.exists() && baseDir.isDirectory()) { // 判断目录是否存在
            // 判断目录是否存在
            File tempFile;
            File[] files = baseDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    tempFile = file;
                    if (tempFile.isDirectory()) {
                        findFiles(tempFile.getAbsolutePath(), targetFileName, fileList);
                    } else if (tempFile.isFile()) {
                        if (wildcardMatch(targetFileName, tempFile.getName())) {
                            // 匹配成功，将文件名添加到结果集
                            fileList.add(tempFile);
                        }
                    }
                }
            }
        }
    }

    /**
     * 通配符匹配
     *
     * @param pattern 通配符模式
     * @param str     待匹配的字符串
     * @return 匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                // 通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1),
                            str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                // 通配符问号?表示匹配任意一个字符
                strIndex++;
                if (strIndex > strLength) {
                    // 表示str中已经没有字符匹配?了。
                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return (strIndex == strLength);
    }
}
