package org.truenewx.tnxjee.service.impl.spec.region.address;

import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spec.InetAddressSet;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 抽象的区划-网络地址集合 来源实现
 *
 * @author jianglei
 * 
 */
public class RegionInetAddressSetMapSourceImpl
        implements RegionInetAddressSetMapSource, InitializingBean {

    private Resource location;
    private Locale locale = Locale.getDefault();
    private String encoding = Strings.ENCODING_UTF8;
    private boolean init;
    private RegionInetAddressSetMapParser parser;
    private Map<String, InetAddressSet> map;

    public void setLocation(Resource location) {
        this.location = location;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setParser(RegionInetAddressSetMapParser parser) {
        this.parser = parser;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    @Override
    public Map<String, InetAddressSet> getMap() {
        if (this.map == null) {
            init();
        }
        return this.map;
    }

    private void init() {
        if (this.location != null) { // 不检查文件是否存在，不存在时抛出异常以便于定位错误
            try {
                // 初始化时解析并缓存
                InputStream in = this.location.getInputStream();
                this.map = this.parser.parse(in, this.locale, this.encoding);
                in.close();
            } catch (Exception e) {
                LogUtil.error(getClass(), e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.init) {
            init();
        }
    }

}
