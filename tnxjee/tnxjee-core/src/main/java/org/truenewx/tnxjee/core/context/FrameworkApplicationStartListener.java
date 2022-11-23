package org.truenewx.tnxjee.core.context;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 框架应用启动侦听器
 *
 * @author jianglei
 */
@Component
public class FrameworkApplicationStartListener implements ApplicationListener<ApplicationStartedEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (FrameworkApplication.STARTING_FILE != null) {
            try {
                FrameworkApplication.STARTING_FILE.close();
                FrameworkApplication.STARTING_FILE = null;
            } catch (Exception e) {
                LogUtil.error(getClass(), e);
            }
        }
    }

}
