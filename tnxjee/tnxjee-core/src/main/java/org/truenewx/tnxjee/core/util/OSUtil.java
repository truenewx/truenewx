package org.truenewx.tnxjee.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.apache.commons.io.IOUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * 操作系统相关工具类
 */
public class OSUtil {

    private OSUtil() {
    }

    public static String JAVA_HOME = System.getProperty("java.home");

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
     */
    public static void executeCommand(String command, BiConsumer<Integer, String> resultConsumer) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            if (!command.endsWith(" &")) {
                try {
                    process.waitFor(1, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {
                }
            }
            consumeResult(process, resultConsumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void consumeResult(Process process, BiConsumer<Integer, String> resultConsumer)
            throws IOException {
        if (resultConsumer != null) {
            int exitValue = -1;
            String result = null;
            try {
                if (process.waitFor(1, TimeUnit.SECONDS)) {
                    exitValue = process.exitValue();
                    if (exitValue == 0) {
                        result = getResult(process);
                    } else {
                        result = getError(process);
                    }
                }
            } catch (InterruptedException ignored) {
            }
            resultConsumer.accept(exitValue, result);
        }
    }

    private static String getResult(Process process) throws IOException {
        InputStream in = process.getInputStream();
        String result = IOUtils.toString(in, getOutputCharset());
        in.close();
        return result;
    }

    private static String getOutputCharset() {
        String os = currentSystem();
        return Strings.OS_WINDOWS.equals(os) ? Strings.ENCODING_GB18030 : Strings.ENCODING_UTF8;
    }

    private static String getError(Process process) throws IOException {
        InputStream in = process.getErrorStream();
        String error = IOUtils.toString(in, getOutputCharset());
        in.close();
        return error;
    }

    /**
     * 执行指定命令行指令
     *
     * @param commands       命令行指令集
     * @param dir            执行目录
     * @param resultConsumer 结果消费者，为null时不等待结果反馈
     */
    public static void executeCommand(String[] commands, File dir, BiConsumer<Integer, String> resultConsumer) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            if (dir != null) {
                dir.mkdirs();
                processBuilder.directory(dir);
            }
            Process process = processBuilder.start();
            if (!" &".equals(commands[commands.length - 1])) {
                try {
                    process.waitFor(1, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {
                }
            }
            consumeResult(process, resultConsumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void killProcesses(String name, String findstr) {
        String os = currentSystem();
        if (Strings.OS_WINDOWS.equals(os)) { // Windows系统无法通过ProcessHandle取得命令行参数，无法比对findstr
            String command = "wmic process where name=\"" + name + "\" get CommandLine,ProcessId";
            executeCommand(command, (exitValue, result) -> {
                if (exitValue == 0) {
                    String[] lines = result.trim().split(Strings.ENTER);
                    if (lines.length > 1) {
                        for (int i = 1; i < lines.length; i++) {
                            String line = lines[i].trim();
                            String[] cells = line.split(" {2,}", 2);
                            if (cells.length > 1) {
                                String commandLine = cells[0];
                                if (commandLine.contains(findstr)) {
                                    Long pid = MathUtil.parseLongObject(cells[1].trim());
                                    if (pid != null) {
                                        executeCommand("taskkill /f /pid " + pid, null);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LogUtil.error(OSUtil.class, "{}\n{}", command, result);
                }
            });
        } else {
            ProcessHandle.allProcesses().filter(ph -> {
                ProcessHandle.Info phi = ph.info();
                Optional<String> commandLineOptional = phi.commandLine();
                if (commandLineOptional.isPresent()) {
                    String commandLine = commandLineOptional.get();
                    return commandLine.contains(name) && commandLine.contains(findstr);
                }
                return false;
            }).forEach(ProcessHandle::destroyForcibly);
        }
    }

}
