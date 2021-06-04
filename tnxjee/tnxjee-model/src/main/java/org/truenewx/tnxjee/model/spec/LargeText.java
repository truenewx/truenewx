package org.truenewx.tnxjee.model.spec;

import org.truenewx.tnxjee.model.entity.unity.Unity;

/**
 * 大文本单体。用于将一般业务实体中的大文本字段独立出来，以使其获得更好的性能表现
 */
public class LargeText implements Unity<String> {

    private String id;
    private String value;

    public LargeText() {
    }

    public LargeText(String id, String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String getId() {
        return this.id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
