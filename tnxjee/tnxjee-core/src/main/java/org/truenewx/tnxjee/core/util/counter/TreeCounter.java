package org.truenewx.tnxjee.core.util.counter;

import java.util.TreeMap;

/**
 * 基于TreeMap的计数器
 *
 * @author jianglei
 * 
 */
public class TreeCounter<K> extends AbstractCounter<K> {

    public TreeCounter() {
        super(new TreeMap<>());
    }

}
