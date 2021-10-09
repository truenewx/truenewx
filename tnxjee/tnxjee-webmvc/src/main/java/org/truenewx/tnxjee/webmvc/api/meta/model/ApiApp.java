package org.truenewx.tnxjee.webmvc.api.meta.model;

import java.util.Map;

/**
 * Api中的应用配置
 */
public class ApiApp {

    private String baseUrl;
    private Map<String, String> subs;

    public ApiApp(String baseUrl, Map<String, String> subs) {
        this.baseUrl = baseUrl;
        this.subs = subs;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public Map<String, String> getSubs() {
        return this.subs;
    }

}
