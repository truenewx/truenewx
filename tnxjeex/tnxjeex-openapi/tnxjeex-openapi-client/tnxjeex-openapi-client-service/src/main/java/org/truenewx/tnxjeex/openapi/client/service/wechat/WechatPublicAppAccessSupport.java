package org.truenewx.tnxjeex.openapi.client.service.wechat;

import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.*;
import java.util.function.Supplier;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.LoggerFactory;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;

/**
 * 微信公众平台（mp.weixin.qq.com）应用访问支持
 *
 * @author jianglei
 */
public abstract class WechatPublicAppAccessSupport extends WechatAppAccessSupport {

    private static final long ACCESS_TOKEN_INTERVAL = 1000 * 60 * 60; // 有效期1小时

    private String accessToken;
    private long accessTokenExpiredTimestamp = 0L;
    private String jsApiTicket;
    private long jsApiTicketExpiredTimestamp = 0L;

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public String decryptUnionId(String encryptedData, String iv, String sessionKey) {
        if (StringUtils.isBlank(encryptedData) || StringUtils.isBlank(iv)
                || StringUtils.isBlank(sessionKey)) {
            return null;
        }
        // 被加密的数据
        byte[] dataBytes = Base64.getDecoder().decode(encryptedData);
        // 加密秘钥
        byte[] sessionKeyBytes = Base64.getDecoder().decode(sessionKey);
        // 偏移量
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        // 如果密钥不足16位就补足
        int base = 16;
        if (sessionKeyBytes.length % base != 0) {
            int groups = sessionKeyBytes.length / base
                    + (sessionKeyBytes.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(sessionKeyBytes, 0, temp, 0, sessionKeyBytes.length);
            sessionKeyBytes = temp;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding",
                    BouncyCastleProvider.PROVIDER_NAME);
            SecretKeySpec spec = new SecretKeySpec(sessionKeyBytes, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivBytes));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
            byte[] resultBytes = cipher.doFinal(dataBytes);
            if (resultBytes != null && resultBytes.length > 0) {
                String json = new String(resultBytes, Strings.ENCODING_UTF8);
                Map<String, Object> result = JsonUtil.json2Map(json);
                @SuppressWarnings("unchecked")
                Map<String, Object> watermark = (Map<String, Object>) result.get("watermark");
                if (watermark == null) {
                    return null;
                }
                if (!getAppId().equals(watermark.get("appid"))) {
                    return null;
                }
                return (String) result.get("unionId");
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public String getUnionId(String openId) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", getAccessToken());
        params.put("openid", openId);
        params.put("lang", Locale.getDefault().toString());
        Map<String, Object> result = get("/cgi-bin/user/info", params);
        return (String) result.get("unionid");
    }

    protected synchronized String getAccessToken() {
        long now = System.currentTimeMillis();
        if (this.accessToken == null || this.accessTokenExpiredTimestamp < now) {
            Map<String, Object> params = new HashMap<>();
            params.put("appid", getAppId());
            params.put("secret", getSecret());
            params.put("grant_type", "client_credential");
            Map<String, Object> result = get("/cgi-bin/token", params);
            this.accessToken = (String) result.get("access_token");
            this.accessTokenExpiredTimestamp = now + ACCESS_TOKEN_INTERVAL;
        }
        return this.accessToken;
    }

    protected synchronized String getJsApiTicket() {
        long now = System.currentTimeMillis();
        if (this.jsApiTicket == null || this.jsApiTicketExpiredTimestamp < now) {
            Map<String, Object> params = new HashMap<>();
            params.put("access_token", getAccessToken());
            params.put("type", "jsapi");
            Map<String, Object> result = get("/cgi-bin/ticket/getticket", params);
            this.jsApiTicket = (String) result.get("ticket");
            this.jsApiTicketExpiredTimestamp = now + ACCESS_TOKEN_INTERVAL; // 临时票据的过期间隔与AccessToken一致
        }
        return this.jsApiTicket;
    }

    public String signJsApiPage(String noncestr, long timestamp, String url) {
        String s = "jsapi_ticket=" + getJsApiTicket() + "&noncestr=" + noncestr + "&timestamp="
                + timestamp + "&url=" + url;
        return EncryptUtil.encryptBySha1(s);
    }

    /**
     * 校验指定文本内容的合法性
     *
     * @param text                 文本内容
     * @param fieldCaptionSupplier 字段名称供应者
     * @throws BusinessException 如果非法
     */
    public void validateTextLegality(String text, Supplier<String> fieldCaptionSupplier)
            throws BusinessException {
        if (StringUtils.isNotBlank(text)) {
            String url = "/wxa/msg_sec_check?access_token=" + getAccessToken();
            Map<String, Object> params = new HashMap<>();
            params.put("content", text);
            Map<String, Object> result = post(url, params);
            validateLegalityResult(result, fieldCaptionSupplier);
        }
    }

    private void validateLegalityResult(Map<String, Object> result,
            Supplier<String> fieldCaptionSupplier) throws BusinessException {
        if (result != null) {
            Integer errcode = (Integer) result.get("errcode");
            if (errcode != null && errcode.intValue() == 87014) {
                String fieldCaption = null;
                if (fieldCaptionSupplier != null) {
                    fieldCaption = fieldCaptionSupplier.get();
                }
                if (StringUtils.isBlank(fieldCaption)) {
                    fieldCaption = Strings.EMPTY;
                }
                throw new BusinessException("error.openapi.client.illegal_content", fieldCaption);
            }
        }
    }

    /**
     * 校验指定图片的合法性
     *
     * @param in                   图片输入流
     * @param fieldCaptionSupplier 字段名称供应者
     * @throws BusinessException 如果非法
     */
    public void validateImageLegality(InputStream in, String mimeType,
            Supplier<String> fieldCaptionSupplier) throws BusinessException {
        String url = "/wxa/img_sec_check?access_token=" + getAccessToken();
        Map<String, Object> result = postFormData(url, in, mimeType);
        validateLegalityResult(result, fieldCaptionSupplier);
    }

}
