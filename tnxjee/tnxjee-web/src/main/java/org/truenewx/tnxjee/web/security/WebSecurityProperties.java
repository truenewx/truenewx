package org.truenewx.tnxjee.web.security;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("tnxjee.web.security")
public class WebSecurityProperties {

    private boolean csrfDisabled;
    private List<String> ignoringPatterns;

    public boolean isCsrfDisabled() {
        return this.csrfDisabled;
    }

    public void setCsrfDisabled(boolean csrfDisabled) {
        this.csrfDisabled = csrfDisabled;
    }

    public List<String> getIgnoringPatterns() {
        return this.ignoringPatterns;
    }

    public void setIgnoringPatterns(List<String> ignoringPatterns) {
        this.ignoringPatterns = ignoringPatterns;
    }

}
