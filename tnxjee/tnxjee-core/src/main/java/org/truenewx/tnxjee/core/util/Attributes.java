package org.truenewx.tnxjee.core.util;

import java.util.Map;

/**
 * 属性集
 */
public interface Attributes {

    String getAttribute(String key);

    <V> V getAttribute(String key, V defaultValue);

    Map<String, String> getAttributes(String... excludedKeys);

}
