package org.truenewx.tnxjee.core.util;

import java.util.*;

/**
 * 以键排序的属性集
 *
 * @author jianglei
 */
public class KeySortedProperties extends Properties {

    @Override
    public Set<Object> keySet() {
        return Collections.synchronizedSet(new TreeSet<>(super.keySet()));
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(keySet());
    }

    @Override
    public Collection<Object> values() {
        Collection<Object> values = new ArrayList<>();
        for (Object key : keySet()) {
            values.add(get(key));
        }
        return Collections.synchronizedCollection(values);
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        Set<Map.Entry<Object, Object>> entrySet = new LinkedHashSet<>();
        for (Object key : keySet()) {
            entrySet.add(new AbstractMap.SimpleEntry<>(key, get(key)));
        }
        return Collections.synchronizedSet(entrySet);
    }

}
