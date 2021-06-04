package org.truenewx.tnxjeex.payment.web;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjeex.payment.core.PaymentManager;
import org.truenewx.tnxjeex.payment.core.PaymentResult;
import org.truenewx.tnxjeex.payment.core.gateway.PaymentGateway;
import org.truenewx.tnxjeex.payment.core.gateway.tenpay.TenpayXmlUtil;

/**
 * 抽象的支付控制器
 *
 * @author jianglei
 */
public abstract class AbstractPayController {

    @Autowired
    protected PaymentManager paymentManager;

    public List<PaymentGateway> getGateways(Terminal terminal) {
        return this.paymentManager.getGateways(terminal);
    }

    @RequestMapping(value = "/result/confirm/{gatewayName}")
    @ConfigAnonymous
    @ResponseBody
    public String confirm(@PathVariable("gatewayName") String gatewayName, HttpServletRequest request) {
        Map<String, String> params = getHttpRequestParams(request);
        if (params != null && params.size() > 0) {
            PaymentResult result = this.paymentManager.notifyResult(gatewayName, true, null, params);
            if (result != null) {
                return result.getResponse();
            }
        }
        return Strings.EMPTY;
    }

    private Map<String, String> getHttpRequestParams(HttpServletRequest request) {
        Map<String, String[]> requestParams = request.getParameterMap();
        if (requestParams != null && requestParams.size() > 0) {
            Map<String, String> params = new HashMap<>();
            for (Entry<String, String[]> entry : requestParams.entrySet()) {
                String name = entry.getKey();
                String[] values = entry.getValue();
                params.put(name, StringUtils.join(values, Strings.COMMA));
            }
            return params;
        } else {
            try {
                Reader reader = request.getReader();
                String xml = IOUtils.toString(reader);
                reader.close();
                return TenpayXmlUtil.doXmlParse(xml);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @RequestMapping(value = "/result/show/{gatewayName}/{terminal}")
    @ConfigAnonymous
    public String show(@PathVariable("gatewayName") String gatewayName,
            @PathVariable(value = "terminal", required = false) String terminal, HttpServletRequest request) {
        Map<String, String> params = getHttpRequestParams(request);
        PaymentResult result = this.paymentManager.notifyResult(gatewayName, false, Terminal.of(terminal), params);
        return result == null ? null : getShowResultName(result);
    }

    protected abstract String getShowResultName(PaymentResult result);

}
