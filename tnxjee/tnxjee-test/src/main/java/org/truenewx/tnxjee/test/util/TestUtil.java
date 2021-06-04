package org.truenewx.tnxjee.test.util;

import java.util.function.Consumer;

import org.mockito.Mockito;
import org.truenewx.tnxjee.core.util.BeanUtil;

/**
 * 单元测试工具类
 *
 * @author jianglei
 */
public class TestUtil {
    /**
     * 是否单元测试环境中
     */
    private static Boolean TESTING;

    private TestUtil() {
    }

    /**
     * @return 是否单元测试环境中
     * @author jianglei
     */
    public static boolean isTesting() {
        if (TESTING == null) {
            StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTrace : stackTraces) {
                String stackString = stackTrace.toString();
                if (stackString.indexOf("junit.runners") >= 0) {
                    TESTING = true;
                    return TESTING;
                }
            }
            TESTING = false;
        }
        return TESTING;
    }

    /**
     * 模拟指定类型的对象到指定目标对象的指定属性上，并执行模拟测试过程
     *
     * @param classToMock 模拟类型
     * @param target      目标对象
     * @param fieldName   目标属性
     * @param consumer    测试过程
     * @param <T>         模拟类型
     */
    public static <T> void mockBeanTo(Class<T> classToMock, Object target, String fieldName, Consumer<T> consumer) {
        target = BeanUtil.getTargetSource(target);
        T originalBean = BeanUtil.getFieldValue(target, fieldName);
        T mockBean = Mockito.mock(classToMock);
        BeanUtil.setFieldValue(target, fieldName, mockBean);
        consumer.accept(mockBean);
        BeanUtil.setFieldValue(target, fieldName, originalBean);
    }

}
