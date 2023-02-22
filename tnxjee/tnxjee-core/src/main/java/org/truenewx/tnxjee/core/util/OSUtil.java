package org.truenewx.tnxjee.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * 操作系统相关工具类
 */
public class OSUtil {

    private OSUtil() {
    }

    public final static String JAVA_HOME = System.getProperty("java.home");

    private final static Map<ProcessHandle, String> PROCESS_COMMAND_LINE_MAPPING = new HashMap<>();
    private static Timer PROCESS_COMMAND_LINE_MAPPING_TIMER;

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
     * @param command       命令行指令
     * @param resultHandler 结果处理器，为null时不等待结果反馈
     * @return 执行结果
     */
    public static <R> R executeCommand(String command, BiFunction<Integer, String, R> resultHandler) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            if (!command.endsWith(" &")) {
                try {
                    process.waitFor(1, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {
                }
            }
            return handleResult(process, resultHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <R> R handleResult(Process process, BiFunction<Integer, String, R> resultHandler)
            throws IOException {
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
        if (resultHandler != null) {
            return resultHandler.apply(exitValue, result);
        }
        return null;
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
     * @param commands      命令行指令集
     * @param dir           执行目录
     * @param resultHandler 结果处理器，为null时不等待结果反馈
     * @return 执行结果
     */
    public static <R> R executeCommand(String[] commands, File dir, BiFunction<Integer, String, R> resultHandler) {
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
            return handleResult(process, resultHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean exitsProcess(String commandPattern, String... commandLinePatterns) {
        List<ProcessHandle> phs = new ArrayList<>();
        forEachProcessHandles(commandPattern, commandLinePatterns, ph -> {
            phs.add(ph);
            return false; // 找到一个匹配则退出遍历
        });
        return phs.size() > 0;
    }

    private static void forEachProcessHandles(String commandPattern, String[] commandLinePatterns,
            Predicate<ProcessHandle> continuePredicate) {
        // 先取得命令样板匹配的所有线程，这一步速度很快，无需考虑提前中止遍历
        List<ProcessHandle> phs = ProcessHandle.allProcesses().filter(ph -> {
            Optional<String> command = ph.info().command();
            return command.isPresent() && StringUtil.wildcardMatch(command.get(), commandPattern);
        }).collect(Collectors.toList());
        // 再尝试匹配命令行样板，这一步可能较慢，需考虑提前中止遍历
        String os = currentSystem();
        for (ProcessHandle ph : phs) {
            if (matchesCommandLinePatterns(os, ph, commandLinePatterns)) {
                // 命令行样板匹配时，判断是否需要继续遍历
                if (!continuePredicate.test(ph)) {
                    return;
                }
            }
        }
    }

    private static boolean matchesCommandLinePatterns(String os, ProcessHandle ph, String[] commandLinePatterns) {
        if (Strings.OS_WINDOWS.equals(os)) {  // Windows系统无法通过ProcessHandle取得命令行数据
            String cachedCommandLine = PROCESS_COMMAND_LINE_MAPPING.get(ph);
            if (cachedCommandLine != null) {
                if (ph.isAlive()) {
                    return StringUtil.wildcardMatchOneOf(cachedCommandLine, commandLinePatterns);
                } else {
                    PROCESS_COMMAND_LINE_MAPPING.remove(ph);
                }
            }
            String command = "wmic process where ProcessId=" + ph.pid() + " get CommandLine"; // 该指令耗时约为2秒
            return executeCommand(command, (exitValue, result) -> {
                if (exitValue == 0) {
                    String[] lines = result.trim().split(Strings.ENTER);
                    if (lines.length > 1) {
                        for (int i = 1; i < lines.length; i++) {
                            String commandLine = lines[i].trim();
                            cacheProcessCommandLine(ph, commandLine);
                            if (StringUtil.wildcardMatchOneOf(commandLine, commandLinePatterns)) {
                                return true;
                            }
                        }
                    }
                } else {
                    cacheProcessCommandLine(ph, Strings.EMPTY);
                    if (result != null) {
                        LogUtil.warn(OSUtil.class,
                                "====== command execute error:\ncommand: {}\nexitValue: {}\nresult: {}",
                                command, exitValue, result);
                    }
                }
                return false;
            });
        } else {
            Optional<String> commandLine = ph.info().commandLine();
            return commandLine.isPresent() && StringUtil.wildcardMatchOneOf(commandLine.get(), commandLinePatterns);
        }
    }

    private static void cacheProcessCommandLine(ProcessHandle ph, String commandLine) {
        PROCESS_COMMAND_LINE_MAPPING.put(ph, commandLine);
        LogUtil.debug(OSUtil.class, "Cached ProcessHandle(pid={}, commandLine={})", ph.pid(), commandLine);
        if (PROCESS_COMMAND_LINE_MAPPING_TIMER == null) {
            PROCESS_COMMAND_LINE_MAPPING_TIMER = new Timer(true);
            PROCESS_COMMAND_LINE_MAPPING_TIMER.schedule(new TimerTask() {
                @Override
                public void run() {
                    CollectionUtil.removeIf(PROCESS_COMMAND_LINE_MAPPING, (ph, commandLine) -> {
                        if (!ph.isAlive()) {
                            LogUtil.debug(OSUtil.class,
                                    "The cached ProcessHandle(pid={}) is not alive, which was cleaned. The current cached count: {}",
                                    ph.pid(), PROCESS_COMMAND_LINE_MAPPING.size() - 1);
                            return true;
                        }
                        return false;
                    });
                }
            }, 1000, 3000);
        }
    }

    public static int killProcesses(String commandPattern, String... commandLinePatterns) {
        List<ProcessHandle> phs = new ArrayList<>();
        forEachProcessHandles(commandPattern, commandLinePatterns, ph -> {
            ph.destroyForcibly();
            phs.add(ph);
            return true;
        });
        return phs.size();
    }

}
