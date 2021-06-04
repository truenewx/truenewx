package org.truenewx.tnxjee.webmvc.api.meta.model;

import java.util.Collection;
import java.util.Map;

import org.truenewx.tnxjee.core.enums.EnumItem;

/**
 * API模型属性元数据
 */
public class ApiModelPropertyMeta {

    private String caption;
    private ApiModelPropertyType type;
    private Map<String, Object> validation;
    private Collection<EnumItem> enums;

    public ApiModelPropertyMeta(String caption, ApiModelPropertyType type) {
        this.caption = caption;
        this.type = type;
    }

    public String getCaption() {
        return this.caption;
    }

    public ApiModelPropertyType getType() {
        return this.type;
    }

    public Map<String, Object> getValidation() {
        return this.validation;
    }

    public void setValidation(Map<String, Object> validation) {
        this.validation = validation;
    }

    public Collection<EnumItem> getEnums() {
        return this.enums;
    }

    public void setEnums(Collection<EnumItem> enums) {
        this.enums = enums;
    }

}
