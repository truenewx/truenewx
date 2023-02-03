package org.truenewx.tnxjee.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 *
 * @author jianglei
 */
public class LogUtil {

    private LogUtil() {
    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static void error(Logger logger, Throwable t) {
        if (logger.isErrorEnabled()) {
            logger.error(t.getLocalizedMessage(), t);
        }
    }

    public static void error(Class<?> clazz, Throwable t) {
        error(getLogger(clazz), t);
    }

    public static void error(Class<?> clazz, String format, Object... args) {
        Logger logger = getLogger(clazz);
        if (logger.isErrorEnabled()) {
            logger.error(format, args);
        }
    }

    public static void warn(Logger logger, Throwable t) {
        if (logger.isWarnEnabled()) {
            logger.warn(t.getLocalizedMessage(), t);
        }
    }

    public static void warn(Class<?> clazz, Throwable t) {
        warn(getLogger(clazz), t);
    }

    public static void warn(Class<?> clazz, String format, Object... args) {
        Logger logger = getLogger(clazz);
        if (logger.isWarnEnabled()) {
            logger.warn(format, args);
        }
    }

    public static void info(Logger logger, Throwable t) {
        if (logger.isInfoEnabled()) {
            logger.info(t.getLocalizedMessage(), t);
        }
    }

    public static void info(Class<?> clazz, Throwable t) {
        info(getLogger(clazz), t);
    }

    public static void info(Class<?> clazz, String format, Object... args) {
        Logger logger = getLogger(clazz);
        if (logger.isInfoEnabled()) {
            logger.info(format, args);
        }
    }

    public static void debug(Logger logger, Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.debug(t.getLocalizedMessage(), t);
        }
    }

    public static void debug(Class<?> clazz, Throwable t) {
        debug(getLogger(clazz), t);
    }

    public static void debug(Class<?> clazz, String format, Object... args) {
        Logger logger = getLogger(clazz);
        if (logger.isDebugEnabled()) {
            logger.debug(format, args);
        }
    }

}
