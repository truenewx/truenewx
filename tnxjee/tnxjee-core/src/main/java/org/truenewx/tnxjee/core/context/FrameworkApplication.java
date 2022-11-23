package org.truenewx.tnxjee.core.context;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.io.CompositeOutputStream;
import org.truenewx.tnxjee.core.io.LockingFile;
import org.truenewx.tnxjee.core.util.IOUtil;

/**
 * 框架应用
 *
 * @author jianglei
 */
public class FrameworkApplication {
    /**
     * 参数前缀：控制台输出文件
     */
    public static final String ARG_PREFIX_SYSTEM_OUT_FILE = "--system.out.file=";
    /**
     * 参数前缀：控制台输出时是否输出至原始控制台
     */
    public static final String ARG_PREFIX_SYSTEM_OUT_ORIGINAL = "--system.out.original=";
    /**
     * 参数前缀：标注正在启动中的文件
     */
    public static final String ARG_PREFIX_STARTING_FILE = "--starting.file=";

    public static final String STARTING_FILE_EXTENSION = ".starting";

    /**
     * 正在启动中的标识文件锁
     */
    public static LockingFile STARTING_FILE;

    public static void run(Class<?> primarySource, String[] args) {
        run(primarySource, args, null);
    }

    /**
     * 运行应用
     *
     * @param primarySource 主来源类型
     * @param args          参数集
     * @param defaultArgs   默认参数映射集，key:参数前缀，value:默认值
     */
    public static void run(Class<?> primarySource, String[] args, Map<String, String> defaultArgs) {
        processArg(args, ARG_PREFIX_SYSTEM_OUT_FILE, defaultArgs, location -> {
            try {
                String original = getArgValue(args, ARG_PREFIX_SYSTEM_OUT_ORIGINAL);
                File file = new File(location);
                IOUtil.createFile(file);
                OutputStream out = new FileOutputStream(file, true);
                if (!Boolean.FALSE.toString().equalsIgnoreCase(original)) {
                    out = new CompositeOutputStream(System.out, out);
                }
                System.setOut(new PrintStream(out, false, StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        processArg(args, ARG_PREFIX_STARTING_FILE, defaultArgs, location -> {
            try {
                File file = new File(location);
                IOUtil.createFile(file);
                STARTING_FILE = new LockingFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        SpringApplication.run(primarySource, args);
    }

    private static void processArg(String[] args, String prefix, Map<String, String> defaultArgs,
            Consumer<String> consumer) {
        String value = getArgValue(args, prefix);
        if (defaultArgs != null && StringUtils.isBlank(value)) {
            value = defaultArgs.get(prefix);
        }
        if (StringUtils.isNotBlank(value)) {
            consumer.accept(value);
        }
    }

    private static String getArgValue(String[] args, String prefix) {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith(prefix)) {
                    args[i] = Strings.EMPTY;
                    return arg.substring(prefix.length());
                }
            }
        }
        return null;
    }

}
