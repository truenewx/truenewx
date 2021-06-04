package org.truenewx.tnxjee.test.service.junit.rules;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.exception.MultiException;
import org.truenewx.tnxjee.service.exception.SingleException;
import org.truenewx.tnxjee.test.junit.rules.TestRuleAdapter;
import org.truenewx.tnxjee.test.service.annotation.TestBusinessException;

/**
 * 期望业务异常的单元测试规则
 *
 * @author jianglei
 */
public class ExpectedBusinessException extends TestRuleAdapter {

    public static final ExpectedBusinessException INSTANCE = new ExpectedBusinessException();

    private ExpectedBusinessException() {
    }

    @Override
    public void evaluate(Statement base, Description description) throws Throwable {
        try {
            base.evaluate();
        } catch (BusinessException e) {
            TestBusinessException tbe = description.getAnnotation(TestBusinessException.class);
            if (tbe != null) {
                String[] expectedCodes = tbe.value();
                Assert.assertEquals(1, expectedCodes.length);
                Assert.assertEquals(expectedCodes[0], e.getCode());
            } else { // 没有@TestBusinessException注解，则抛给上层处理
                throw e;
            }
        } catch (MultiException me) {
            TestBusinessException tbe = description.getAnnotation(TestBusinessException.class);
            if (tbe != null) {
                String[] expectedCodes = tbe.value();
                Assert.assertEquals(expectedCodes.length, me.getTotal());
                for (SingleException se : me) {
                    if (se instanceof BusinessException) {
                        BusinessException be = (BusinessException) se;
                        Assert.assertTrue(ArrayUtils.contains(expectedCodes, be.getCode()));
                    }
                }
            } else { // 没有@TestBusinessException注解，则抛给上层处理
                throw me;
            }
        }
    }
}
