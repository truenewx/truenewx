package org.truenewx.tnxsample.fss.service.strategy;

import org.truenewx.tnxjee.model.spec.user.IntegerUserIdentity;
import org.truenewx.tnxjeex.fss.service.FssAccessStrategy;
import org.truenewx.tnxjeex.fss.service.model.FssProvider;

/**
 * 抽象文件存储访问策略
 *
 * @author jianglei
 */
public abstract class AbstractFssAccessStrategy
        implements FssAccessStrategy<IntegerUserIdentity> {

    @Override
    public FssProvider getProvider() {
        return FssProvider.OWN;
    }

}
