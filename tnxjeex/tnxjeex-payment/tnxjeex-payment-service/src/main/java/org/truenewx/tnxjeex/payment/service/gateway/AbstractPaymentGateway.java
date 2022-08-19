package org.truenewx.tnxjeex.payment.service.gateway;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjee.service.spec.region.RegionNationCodes;

/**
 * 抽象的支付网关
 *
 * @author jianglei
 */
public abstract class AbstractPaymentGateway implements PaymentGatewayAdapter {

    private String name;
    private boolean active;
    private String nationCode = RegionNationCodes.CHINA;
    private Terminal[] terminals;
    private String logoUrl;
    private boolean refundable;
    private String resultConfirmUrl;
    private String resultShowUrl;

    protected final Logger logger = LogUtil.getLogger(getClass());

    @Override
    public String getName() {
        return StringUtils.isBlank(this.name) ? getChannel().name().toLowerCase() : this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getNationCode() {
        return this.nationCode;
    }

    public void setNationCode(String nationCode) {
        this.nationCode = nationCode;
    }

    @Override
    public Terminal[] getTerminals() {
        return this.terminals;
    }

    public void setTerminals(Terminal... terminals) {
        this.terminals = terminals;
    }

    @Override
    public String getLogoUrl() {
        return this.logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    @Override
    public boolean isRefundable() {
        return this.refundable;
    }

    public void setRefundable(boolean refundable) {
        this.refundable = refundable;
    }

    protected String getResultConfirmUrl() {
        return replaceName(this.resultConfirmUrl);
    }

    private String replaceName(String url) {
        if (url != null && url.contains("{name}")) {
            url = url.replaceAll("\\{name\\}", getName());
        }
        return url;
    }

    public void setResultConfirmUrl(String resultConfirmUrl) {
        this.resultConfirmUrl = resultConfirmUrl;
    }

    protected String getResultShowUrl() {
        return replaceName(this.resultShowUrl);
    }

    public void setResultShowUrl(String resultShowUrl) {
        this.resultShowUrl = resultShowUrl;
    }

    @Override
    public String requestRefund(String gatewayPaymentNo, BigDecimal paymentAmount, String refundNo,
            String refundAmount) {
        // 默认不支持退款
        throw new UnsupportedOperationException();
    }

}
