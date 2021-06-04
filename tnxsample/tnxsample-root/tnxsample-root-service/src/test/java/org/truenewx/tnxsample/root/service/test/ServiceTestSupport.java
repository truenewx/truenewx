package org.truenewx.tnxsample.root.service.test;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.truenewx.tnxjee.Framework;
import org.truenewx.tnxjee.test.service.support.ServiceSpringTestSupport;
import org.truenewx.tnxsample.root.AppRoot;

/**
 * Service测试支持
 *
 * @author jianglei
 */
@ActiveProfiles("junit")
@ContextConfiguration(classes = { Framework.class, AppRoot.class })
public abstract class ServiceTestSupport extends ServiceSpringTestSupport {
}
