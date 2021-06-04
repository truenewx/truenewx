package org.truenewx.tnxjee.webmvc.util;

import java.lang.annotation.Annotation;

/**
 * Bean属性元数据
 */
public class BeanPropertyMeta {

    private String name;
    private boolean multi;
    private Class<?> rawClass;
    private Annotation annotation;

    public BeanPropertyMeta(String name, boolean multi, Class<?> rawClass) {
        this.name = name;
        this.multi = multi;
        this.rawClass = rawClass;
    }

    public BeanPropertyMeta(String name, boolean multi, Annotation annotation) {
        this.name = name;
        this.multi = multi;
        this.rawClass = String.class;
        this.annotation = annotation;
    }

    public String getName() {
        return this.name;
    }

    public boolean isMulti() {
        return this.multi;
    }

    public Class<?> getRawClass() {
        return this.rawClass;
    }

    public Annotation getAnnotation() {
        return this.annotation;
    }
}
