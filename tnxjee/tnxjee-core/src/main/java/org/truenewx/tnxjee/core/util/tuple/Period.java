package org.truenewx.tnxjee.core.util.tuple;

import java.util.Date;

/**
 * 时间区间
 *
 * @author jianglei
 * 
 */
public class Period {

    private Date earlier;
    private Date later;

    /**
     * @param earlier 早一点的时间
     * @param later   晚一点的时间
     */
    public Period(Date earlier, Date later) {
        this.earlier = earlier;
        this.later = later;
    }

    public Date getEarlier() {
        return this.earlier;
    }

    public Date getLater() {
        return this.later;
    }

}
