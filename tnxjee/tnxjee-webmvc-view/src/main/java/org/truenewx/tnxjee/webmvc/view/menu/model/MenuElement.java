package org.truenewx.tnxjee.webmvc.view.menu.model;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.CollectionUtil;

/**
 * 菜单元素
 */
public abstract class MenuElement implements Serializable {

    private static final long serialVersionUID = -3827849614788066392L;

    /**
     * 可见环境集合
     */
    private Set<String> profiles = new HashSet<>();
    /**
     * 显示名称映射集
     */
    private Map<Locale, String> captions = new HashMap<>();
    /**
     * 描述映射集
     */
    private Map<Locale, String> descs = new HashMap<>();
    /**
     * 选项映射集
     */
    private Map<String, Object> options = new HashMap<>();

    public Set<String> getProfiles() {
        return this.profiles;
    }

    public void setProfiles(Set<String> profiles) {
        CollectionUtil.reset(profiles, this.profiles);
    }

    public Map<Locale, String> getCaptions() {
        return this.captions;
    }

    public void setCaptions(Map<Locale, String> captions) {
        CollectionUtil.reset(captions, this.captions);
    }

    public Map<Locale, String> getDescs() {
        return this.descs;
    }

    public void setDescs(Map<Locale, String> descs) {
        CollectionUtil.reset(descs, this.descs);
    }

    public Map<String, Object> getOptions() {
        return this.options;
    }

    public void setOptions(Map<String, Object> options) {
        CollectionUtil.reset(options, this.options);
    }

    //////

    public void setCaption(String caption) {
        this.captions.put(Locale.getDefault(), caption);
    }

    public String getCaption() {
        return this.captions.get(Locale.getDefault());
    }

    public void setDesc(String desc) {
        this.descs.put(Locale.getDefault(), desc);
    }

    public String getDesc() {
        return this.descs.get(Locale.getDefault());
    }

    protected <T extends MenuElement> T cloneTo(T target) {
        CollectionUtil.reset(this.profiles, target.getProfiles());
        CollectionUtil.reset(this.captions, target.getCaptions());
        CollectionUtil.reset(this.descs, target.getDescs());
        CollectionUtil.reset(this.options, target.getOptions());
        return target;
    }

    public boolean matchesProfile(String profile) {
        return this.profiles.isEmpty() || StringUtils.isBlank(profile) || this.profiles.contains(profile);
    }

}
