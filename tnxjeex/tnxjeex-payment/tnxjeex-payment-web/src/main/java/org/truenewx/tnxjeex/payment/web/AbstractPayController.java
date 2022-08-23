package org.truenewx.tnxjeex.payment.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.http.HttpRequestDataProvider;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjee.webmvc.servlet.http.HttpServletRequestDataProvider;
import org.truenewx.tnxjeex.payment.gateway.PaymentManager;
import org.truenewx.tnxjeex.payment.model.PaymentDefinition;
import org.truenewx.tnxjeex.payment.model.PaymentRequestParameter;
import org.truenewx.tnxjeex.payment.model.PaymentResult;

/**
 * 抽象的支付控制器
 *
 * @author jianglei
 */
public abstract class AbstractPayController {

    @Autowired
    protected PaymentManager paymentManager;

    // 不配置权限限定，必须子类覆写进行限定
    @PostMapping("/prepare/{gatewayName}")
    @ResponseBody
    public PaymentRequestParameter prepare(@PathVariable("gatewayName") String gatewayName,
            @RequestBody PaymentDefinition definition) {
        return this.paymentManager.getRequestParameter(gatewayName, definition);
    }

    @RequestMapping(value = "/result/confirm/{gatewayName}")
    @ConfigAnonymous
    @ResponseBody
    public String confirmResult(@PathVariable("gatewayName") String gatewayName, HttpServletRequest request,
            HttpServletResponse response) {
        HttpRequestDataProvider notifyDataProvider = new HttpServletRequestDataProvider(request);
        PaymentResult result = this.paymentManager.notifyResult(gatewayName, true, notifyDataProvider);
        if (result != null) {
            response.setStatus(result.getResponseStatus());
            return result.getResponseBody();
        }
        return Strings.EMPTY;
    }

    @RequestMapping(value = "/result/show/{gatewayName}/{terminal}")
    @ConfigAnonymous
    public ModelAndView showResult(@PathVariable("gatewayName") String gatewayName,
            @PathVariable(value = "terminal", required = false) String terminal, HttpServletRequest request) {
        HttpRequestDataProvider notifyDataProvider = new HttpServletRequestDataProvider(request);
        PaymentResult result = this.paymentManager.notifyResult(gatewayName, false, notifyDataProvider);
        return resolveShowResult(request, result, Terminal.of(terminal));
    }

    protected abstract ModelAndView resolveShowResult(HttpServletRequest request, PaymentResult result,
            Terminal terminal);

}
