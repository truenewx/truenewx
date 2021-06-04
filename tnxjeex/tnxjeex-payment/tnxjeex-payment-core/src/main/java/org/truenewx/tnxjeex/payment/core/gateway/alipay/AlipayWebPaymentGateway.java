package org.truenewx.tnxjeex.payment.core.gateway.alipay;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjee.model.spec.enums.Program;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.payment.core.gateway.PaymentExceptionCodes;

/**
 * 支付宝网页支付网关
 *
 * @author jianglei
 */
public class AlipayWebPaymentGateway extends AlipayPaymentGateway {

    private String privateKey;

    public AlipayWebPaymentGateway() {
        setTerminals(new Terminal(Program.WEB, null, null));
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    protected void sign(SortedMap<String, String> params) {
        StringBuffer sb = new StringBuffer();
        Set<Entry<String, String>> entrySet = params.entrySet();
        for (Entry<String, String> entry : entrySet) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (StringUtils.isNotBlank(v) && !"sign".equals(k) && !"key".equals(k) && !"sign_type".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        String sign = EncryptUtil.encryptByMd5(sb.substring(0, sb.lastIndexOf("&")) + this.privateKey);
        params.put("sign_type", "MD5"); // 签名类型,默认：MD5
        params.put("sign", sign);
    }

    @Override
    protected void validateSign(Map<String, String> params) throws BusinessException {
        StringBuffer sb = new StringBuffer();
        Set<Entry<String, String>> entrySet = params.entrySet();
        for (Entry<String, String> entry : entrySet) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (StringUtils.isNotBlank(v) && !"sign".equals(k) && !"key".equals(k) && !"sign_type".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        String sign = EncryptUtil.encryptByMd5(sb.substring(0, sb.lastIndexOf("&")) + this.privateKey);
        if (!sign.equals(params.get("sign").toLowerCase())) {
            throw new BusinessException(PaymentExceptionCodes.SIGN_FAIL);
        }
    }

}
