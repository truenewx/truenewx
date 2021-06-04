package org.truenewx.tnxjee.core.enums;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.tuple.Binary;
import org.truenewx.tnxjee.core.util.tuple.Binate;

/**
 * 枚举字典
 *
 * @author jianglei
 */
public class EnumDict {

    private Locale locale;
    private Map<Binate<String, String>, EnumType> types = new HashMap<>();

    public EnumDict(Locale locale) {
        if (locale == null) {
            this.locale = Locale.getDefault();
        } else {
            this.locale = locale;
        }
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void addType(EnumType type) {
        if (type != null) {
            String subname = type.getSubname();
            if (StringUtils.isBlank(subname)) {
                subname = null;
            }
            Binate<String, String> key = new Binary<>(type.getName(), subname);
            this.types.put(key, type);
        }
    }

    public EnumType getType(String name) {
        return getType(name, null);
    }

    public EnumType getType(String name, String subname) {
        if (StringUtils.isBlank(subname)) {
            subname = null;
        }
        Binate<String, String> key = new Binary<>(name, subname);
        return this.types.get(key);
    }
}
