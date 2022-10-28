package org.truenewx.tnxjee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.IOUtil;

/**
 * 框架信息
 *
 * @author jianglei
 */
@ComponentScan(basePackageClasses = Framework.class)
public class Framework {

    /**
     * 框架名称
     */
    public static final String NAME = "tnxjee";
    public static final String ARG_SYSTEM_OUT_FILE_PREFIX = "--system.out.file=";

    public static void run(Class<?> primarySource, String... args) throws IOException {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith(ARG_SYSTEM_OUT_FILE_PREFIX)) {
                    String location = arg.substring(ARG_SYSTEM_OUT_FILE_PREFIX.length());
                    if (StringUtils.isNotBlank(location)) {
                        File file = new File(location);
                        IOUtil.createFile(file);
                        FileOutputStream out = new FileOutputStream(file, true);
                        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
                        args[i] = Strings.EMPTY;
                    }
                }
            }
        }
        SpringApplication.run(primarySource, args);
    }

}
