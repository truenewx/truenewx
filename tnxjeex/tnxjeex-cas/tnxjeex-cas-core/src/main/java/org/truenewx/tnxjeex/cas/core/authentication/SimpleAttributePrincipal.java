package org.truenewx.tnxjeex.cas.core.authentication;

import java.util.Map;

import org.jasig.cas.client.authentication.AttributePrincipal;

/**
 * 便于序列化和反序列化的简单AttributePrincipal实现
 */
public class SimpleAttributePrincipal implements AttributePrincipal {

    private static final long serialVersionUID = 950876983210601636L;

    private String name;
    private Map<String, Object> attributes;

    @Override
    public String getProxyTicketFor(String service) {
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
