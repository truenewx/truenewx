package org.truenewx.tnxjee.core.config;

/**
 * 应用的对外门面
 */
public class AppFacade {

    private String name;
    private String caption;
    private String contextUri;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getContextUri() {
        return this.contextUri;
    }

    public void setContextUri(String contextUri) {
        this.contextUri = contextUri;
    }

}
