package org.truenewx.tnxjeex.seata.rm.tcc;

import java.util.Hashtable;
import java.util.Map;

/**
 * Seata上下文
 */
public class TccContext {

    private Map<String, Object> xidContext = new Hashtable<>();

    public void put(String xid, Object value) {
        if (xid != null) {
            this.xidContext.put(xid, value);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T remove(String xid) {
        if (xid != null) {
            return (T) this.xidContext.remove(xid);
        }
        return null;
    }

}
