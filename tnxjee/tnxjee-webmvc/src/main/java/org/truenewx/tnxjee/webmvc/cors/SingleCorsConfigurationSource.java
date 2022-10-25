package org.truenewx.tnxjee.webmvc.cors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * 单个配置的CORS配置源
 */
@Component
public class SingleCorsConfigurationSource implements CorsConfigurationSource, CorsConfigurationUpdater {

    private CorsConfiguration corsConfiguration;

    public void setCorsConfiguration(CorsConfiguration corsConfiguration) {
        this.corsConfiguration = corsConfiguration;
    }

    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        return this.corsConfiguration;
    }

    @Override
    public void addAllowedOrigin(String origin) {
        this.corsConfiguration.addAllowedOrigin(origin);
    }

}
