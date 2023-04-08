package org.truenewx.tnxjee.core.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 复合速率估算器
 *
 * @author jianglei
 */
public class CompositeRateEstimator<E extends Enum<E>> {

    private final Map<E, RateEstimator> estimators = new HashMap<>();

    public void setDefaultRate(E type, long defaultRate) {
        // 覆盖已有
        this.estimators.put(type, new RateEstimator(defaultRate));
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

    protected final void forEach(BiConsumer<E, Long> consumer) {
        this.estimators.forEach((type, estimator) -> consumer.accept(type, estimator.getEstimatedRate()));
    }

}
