package org.truenewx.tnxjee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.truenewx.tnxjee.core.Strings;
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
     * 参数前缀：标注正在启动中的文件
     */
    public static final String ARG_PREFIX_STARTING_FILE = "--starting.file=";
    /**
     * 正在启动中的标识文件锁
     */
    public static LockingFile STARTING_FILE;

    public static void run(Class<?> primarySource, String... args) {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                processArg(args, i, ARG_PREFIX_SYSTEM_OUT_FILE, location -> {
                    try {
                        File file = new File(location);
                        IOUtil.createFile(file);
                        FileOutputStream out = new FileOutputStream(file, true);
                        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                processArg(args, i, ARG_PREFIX_STARTING_FILE, location -> {
                    try {
                        File file = new File(location);
                        IOUtil.createFile(file);
                        STARTING_FILE = new LockingFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        SpringApplication.run(primarySource, args);
    }

    private static void processArg(String[] args, int i, String prefix, Consumer<String> consumer) {
        String arg = args[i];
        if (arg.startsWith(prefix)) {
            String value = arg.substring(prefix.length());
            if (StringUtils.isNotBlank(value)) {
                consumer.accept(value);
                args[i] = Strings.EMPTY;
            }
        }
    }

}
