package org.truenewx.tnxjee.test.support;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Spring上下文环境测试支持
 *
 * @author jianglei
 */
@ExtendWith({ SpringExtension.class, TestExtension.class })
@SpringBootTest
public abstract class SpringTestSupport implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    @Override
    public final void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
