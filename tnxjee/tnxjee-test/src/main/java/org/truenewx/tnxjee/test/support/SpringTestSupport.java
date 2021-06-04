package org.truenewx.tnxjee.test.support;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.truenewx.tnxjee.test.junit.rules.LogCaption;

/**
 * JUnit4+Spring环境测试
 *
 * @author jianglei
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class SpringTestSupport extends AbstractJUnit4SpringContextTests {

    @Rule
    public LogCaption logCaption = LogCaption.DEFAULT;

}
