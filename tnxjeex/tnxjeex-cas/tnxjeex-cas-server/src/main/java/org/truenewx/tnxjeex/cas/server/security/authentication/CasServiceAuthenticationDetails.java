package org.truenewx.tnxjeex.cas.server.security.authentication;

import java.io.Serializable;

public class CasServiceAuthenticationDetails implements Serializable {

    private static final long serialVersionUID = -8966249391535990582L;

    private String service;
    private String scope;
    private String ip;

    public CasServiceAuthenticationDetails(String service, String scope, String ip) {
        this.service = service;
        this.scope = scope;
        this.ip = ip;
    }

    public String getService() {
        return this.service;
    }

    public String getScope() {
        return this.scope;
    }

    public String getIp() {
        return this.ip;
    }
}
