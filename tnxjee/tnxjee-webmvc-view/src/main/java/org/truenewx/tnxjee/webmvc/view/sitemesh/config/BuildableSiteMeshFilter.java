package org.truenewx.tnxjee.webmvc.view.sitemesh.config;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

import java.util.function.Consumer;

/**
 * 可构建的SiteMesh过滤器
 */
public class BuildableSiteMeshFilter extends ConfigurableSiteMeshFilter {

    private Consumer<SiteMeshFilterBuilder> buildConsumer;

    public BuildableSiteMeshFilter(Consumer<SiteMeshFilterBuilder> buildConsumer) {
        this.buildConsumer = buildConsumer;
    }

    @Override
    protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
        if (this.buildConsumer != null) {
            this.buildConsumer.accept(builder);
        }
    }

}
