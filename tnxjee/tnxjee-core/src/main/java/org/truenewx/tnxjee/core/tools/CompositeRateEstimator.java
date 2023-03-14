package org.truenewx.tnxjee.core.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * 复合速率估算器
 *
 * @author jianglei
 */
public class CompositeRateEstimator<E extends Enum<E>> {

    private Map<E, RateEstimator> estimators = new HashMap<>();

    public void support(E type, long defaultRate) {
        RateEstimator estimator = this.estimators.get(type);
        if (estimator == null) {
            estimator = new RateEstimator(defaultRate);
            this.estimators.put(type, estimator);
        }
    }

    public Long addSample(E type, long variance, long times) {
        return getEstimator(type).addSample(variance, times);
    }

    private RateEstimator getEstimator(E type) {
        RateEstimator estimator = this.estimators.get(type);
        if (estimator == null) {
            estimator = new RateEstimator();
            this.estimators.put(type, estimator);
        }
        return estimator;
    }

    public Long getEstimatedRate(E type) {
        return getEstimator(type).getEstimatedRate();
    }

}
