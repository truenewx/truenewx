package org.truenewx.tnxjeex.payment.core;

import java.math.BigDecimal;
import java.util.Currency;

import org.truenewx.tnxjee.model.spec.Terminal;

/**
 * 支付定义
 *
 * @author jianglei
 */
public class PaymentDefinition {

    private Terminal terminal;
    private String orderNo;
    private BigDecimal amount;
    private Currency currency;
    private String description;
    private String payerIp;
    private String target;

    public Terminal getTerminal() {
        return this.terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPayerIp() {
        return this.payerIp;
    }

    public void setPayerIp(String payerIp) {
        this.payerIp = payerIp;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
