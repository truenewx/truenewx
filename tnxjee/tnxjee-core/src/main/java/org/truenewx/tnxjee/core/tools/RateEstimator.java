package org.truenewx.tnxjee.core.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import org.apache.commons.collections4.queue.CircularFifoQueue;

/**
 * 速率估算器
 *
 * @author jianglei
 */
public class RateEstimator {

    private long defaultRate;
    private Queue<Long> rateQueue = new CircularFifoQueue<>(12);

    public RateEstimator(long defaultRate) {
        this.defaultRate = defaultRate;
    }

    public void add(long rate) {
        this.rateQueue.add(rate);
    }

    public long getEstimatedRate() {
        int count = this.rateQueue.size();
        if (0 < count && count < 5) { // 样本数量小于5个，则简单取均值
            long total = 0;
            for (Long rate : this.rateQueue) {
                total += rate;
            }
            return total / this.rateQueue.size();
        } else if (count >= 5) { // 样本数量大于等于5个，则去掉最小值和最大值后取均值
            List<Long> list = new ArrayList<>(this.rateQueue);
            Collections.sort(list);
            long total = 0;
            for (int i = 1; i < list.size() - 1; i++) {
                total += list.get(i);
            }
            return total / (list.size() - 2);
        }
        return this.defaultRate;
    }

}
