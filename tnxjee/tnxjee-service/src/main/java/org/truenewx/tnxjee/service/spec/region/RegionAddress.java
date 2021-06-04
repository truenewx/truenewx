package org.truenewx.tnxjee.service.spec.region;

/**
 * 带行政区划代码的地址
 *
 * @author jianglei
 */
public class RegionAddress {

    private Region region;
    private String detail;

    public RegionAddress(Region region, String detail) {
        this.region = region;
        this.detail = detail;
    }

    public Region getRegion() {
        return this.region;
    }

    public String getDetail() {
        return this.detail;
    }
}
