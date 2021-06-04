package org.truenewx.tnxjee.core.util.tuple;

/**
 * 三元的（由三个元素组成的）
 * 
 * @author jianglei
 * 
 * @param <L>
 *            左元类型
 * @param <M>
 *            中元类型
 * @param <R>
 *            右元类型
 */
public interface Triple<L, M, R> extends Binate<L, R> {
    /**
     * 获取中元
     * 
     * @return 中元
     */
    M getMiddle();

}
