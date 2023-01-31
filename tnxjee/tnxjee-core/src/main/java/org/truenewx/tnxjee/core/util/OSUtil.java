package org.truenewx.tnxjee.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.apache.commons.io.IOUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * 操作系统相关工具类
 */
public class OSUtil {

    private OSUtil() {
    }

    public static String currentSystem() {
        String name = System.getProperty("os.name").toLowerCase();
        if (name.contains(Strings.OS_WINDOWS)) {
            return Strings.OS_WINDOWS;
        } else if (name.contains(Strings.OS_ANDROID)) {
            return Strings.OS_ANDROID;
        } else if (name.contains(Strings.OS_IOS)) {
            return Strings.OS_IOS;
        } else if (name.contains(Strings.OS_MAC)) {
            return Strings.OS_MAC;
        }
        return Strings.OS_LINUX;
    }

    /**
     * 执行指定命令行指令
     *
     * @param command        命令行指令
     * @param resultConsumer 结果消费者，为null时不等待结果反馈
     * @throws IOException 如果执行过程出现错误
     */
    public static void executeCommand(String command, BiConsumer<String, Long> resultConsumer)
            throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        consumeResult(process, resultConsumer);
    }

    private static void consumeResult(Process process, BiConsumer<String, Long> resultConsumer)
            throws IOException {
        if (resultConsumer != null) {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                LogUtil.error(OSUtil.class, e);
            }
            String result = getResult(process);
            resultConsumer.accept(result, process.pid());
        }
    }

    public static String getResult(Process process) throws IOException {
        InputStream in = process.getInputStream();
        String result = IOUtils.toString(in, getOutputCharset());
        in.close();
        return result;
    }

    private static String getOutputCharset() {
        String os = currentSystem();
        return Strings.OS_WINDOWS.equals(os) ? Strings.ENCODING_GB18030 : Strings.ENCODING_UTF8;
    }

    public static String getError(Process process) throws IOException {
        InputStream in = process.getErrorStream();
        String error = IOUtils.toString(in, getOutputCharset());
        in.close();
        return error;
    }

    /**
     * 执行指定命令行指令
     *
     * @param commands       命令行指令集
     * @param resultConsumer 结果消费者，为null时不等待结果反馈
     * @throws IOException 如果执行过程出现错误
     */
    public static void executeCommand(String[] commands, BiConsumer<String, Long> resultConsumer)
            throws IOException {
        Process process = Runtime.getRuntime().exec(commands);
        consumeResult(process, resultConsumer);
    }

    public static String executeCommand(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        return getResult(process);
    }

    public static List<Long> findPids(String name, String findstr) {
        List<Long> pids = new ArrayList<>();
        try {
            String os = currentSystem();
            if (Strings.OS_WINDOWS.equals(os)) {
                String result = executeCommand("wmic process where name=\"" + name + "\" get CommandLine,ProcessId");
                String[] lines = result.trim().split(Strings.ENTER);
                if (lines.length > 1) {
                    String pattern = Strings.ASTERISK + findstr + Strings.ASTERISK;
                    for (int i = 1; i < lines.length; i++) {
                        String line = lines[i].trim();
                        String[] cells = line.split(" {2,}", 2);
                        if (cells.length > 1) {
                            String commandLine = cells[0];
                            if (StringUtil.wildcardMatch(commandLine, pattern)) {
                                Long pid = MathUtil.parseLongObject(cells[1].trim());
                                if (pid != null) {
                                    pids.add(pid);
                                }
                            }
                        }
                    }
                }
            } else {
                String result = executeCommand("ps -ef |grep " + name + " |grep " + findstr);
                String[] cells = result.split(" {2,}", 3);
                if (cells.length > 1) {
                    Long pid = MathUtil.parseLongObject(cells[1].trim());
                    if (pid != null) {
                        pids.add(pid);
                    }
                }
            }
        } catch (IOException e) {
            LogUtil.error(OSUtil.class, e);
        }
        return pids;
    }

}
