package org.truenewx.tnxjee.webmvc.api.region;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.truenewx.tnxjee.service.spec.region.Region;
import org.truenewx.tnxjee.service.spec.region.RegionSource;
import org.truenewx.tnxjee.webmvc.http.annotation.ResultFilter;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;

@RequestMapping("/api/region")
public abstract class RegionControllerSupport {

    @Autowired(required = false)
    private RegionSource source;

    @GetMapping("/{regionCode}")
    @ConfigAnonymous
    @ResultFilter(type = Region.class, included = { "group", "code", "caption", "level", "subs", "includingSub" })
    public Region detail(@PathVariable("regionCode") String regionCode, HttpServletRequest request) {
        return this.source.getRegion(regionCode, request.getLocale());
    }

}
