package org.truenewx.tnxjee.test.junit.rules;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 输出@Caption注解日志的单元测试规则
 *
 * @author jianglei
 */
public class LogCaption extends TestRuleAdapter {
    /**
     * 默认实例
     */
    public static LogCaption DEFAULT = new LogCaption(true);
    /**
     * 日志输出是否附加测试类名称
     */
    private boolean appendTestClassName;

    public LogCaption(boolean appendTestClassName) {
        this.appendTestClassName = appendTestClassName;
    }

    @Override
    public void evaluate(Statement base, Description description) throws Throwable {
        Caption caption = description.getAnnotation(Caption.class);
        if (caption != null) {
            Class<?> testClass = description.getTestClass();
            Logger logger = LogUtil.getLogger(getClass());
            if (logger.isInfoEnabled()) {
                String info;
                if (this.appendTestClassName) {
                    info = new StringBuffer("[").append(testClass.getSimpleName()).append("]")
                            .append(caption.value()).toString();
                } else {
                    info = caption.value();
                }
                logger.info(info);
            }
        }
        base.evaluate();
    }

}
