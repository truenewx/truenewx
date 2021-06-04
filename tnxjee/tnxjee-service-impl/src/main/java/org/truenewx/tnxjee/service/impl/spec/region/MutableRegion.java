package org.truenewx.tnxjee.service.impl.spec.region;

import org.truenewx.tnxjee.service.spec.region.Region;

/**
 * 内容可变的行政区划
 *
 * @author jianglei
 */
public class MutableRegion extends Region {

    public MutableRegion() {
        super(null, null);
    }

    @Override
    public void setCode(String code) {
        super.setCode(code);
    }

    @Override
    public void setCaption(String caption) {
        super.setCaption(caption);
    }

    @Override
    public void setSuffix(String suffix) {
        super.setSuffix(suffix);
    }

    @Override
    public void setGroup(String group) {
        super.setGroup(group);
    }

    public void setSubs(MutableRegion[] subs) {
        for (Region sub : subs) {
            addSub(sub);
        }
    }

}
