package org.truenewx.tnxjee.core.util.counter;

import java.util.HashMap;

/**
 * 基于HashMap的计数器
 *
 * @author jianglei
 * 
 */
public class HashCounter<K> extends AbstractCounter<K> {

    public HashCounter() {
        super(new HashMap<>());
    }

}
