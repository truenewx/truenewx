package org.truenewx.tnxjee.test.support;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 单元测试扩展
 *
 * @author jianglei
 */
public class TestExtension implements BeforeTestExecutionCallback {

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        String displayName = context.getDisplayName();
        if (StringUtils.isNotBlank(displayName)) {
            String methodName = context.getRequiredTestMethod().getName();
            LogUtil.info(context.getRequiredTestClass(), methodName + "() -> " + displayName);
        }
    }

}
