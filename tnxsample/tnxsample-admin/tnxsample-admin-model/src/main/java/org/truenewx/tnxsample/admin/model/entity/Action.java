package org.truenewx.tnxsample.admin.model.entity;

import java.util.Map;

import org.truenewx.tnxjee.model.ValueModel;

/**
 * 操作
 *
 * @author jianglei
 */
public class Action implements ValueModel {

    private String caption;
    private String url;
    private String method;
    private Map<String, Object> params;

    public Action() {
    }

    public Action(String caption, String url, String method, Map<String, Object> params) {
        this.caption = caption;
        this.url = url;
        this.method = method;
        this.params = params;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

}
