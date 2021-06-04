package org.truenewx.tnxjeex.openapi.client.web.controller;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.core.util.function.ProfileSupplier;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjee.webmvc.view.util.WebViewUtil;
import org.truenewx.tnxjeex.openapi.client.model.wechat.*;
import org.truenewx.tnxjeex.openapi.client.service.NoSuchMessageHandlerException;
import org.truenewx.tnxjeex.openapi.client.service.wechat.WechatMessageListener;

/**
 * 微信开放接口回调控制器支持
 *
 * @author jianglei
 */
public abstract class WechatCallbackControllerSupport {

    private static final String FORWARD_ATTRIBUTE_NAME = "tnxjeex_openapi_callback_forward";

    @Autowired
    private WechatMessageListener listener;
    @Autowired
    private ProfileSupplier profileSupplier;

    @RequestMapping("/callback")
    @ConfigAnonymous
    @ResponseBody
    public String callback(HttpServletRequest request, HttpServletResponse response) {
        if (checkSignature(request)) {
            Map<String, String> parameters = getParameters(request);
            WechatMessage message = getMessage(parameters);
            if (message == null) { // 无法解析出消息的，为验证访问，直接返回echostr参数
                return request.getParameter("echostr");
            }
            try {
                WechatMessage reply = this.listener.onReceived(message);
                if (reply != null) {
                    return toXml(reply);
                }
            } catch (NoSuchMessageHandlerException e) {
                // 没有转发标记属性才转发，以避免转发给自身导致无限转发
                if (request.getAttribute(FORWARD_ATTRIBUTE_NAME) == null) {
                    String forwardUrl = getForwardUrl();
                    if (StringUtils.isNotBlank(forwardUrl)) {
                        try {
                            request.setAttribute(FORWARD_ATTRIBUTE_NAME, Boolean.TRUE); // 设置转发标记属性
                            WebViewUtil.forward(request, response, forwardUrl);
                        } catch (ServletException | IOException ex) {
                            LoggerFactory.getLogger(getClass()).error(ex.getMessage(), ex);
                        }
                    }
                }
            }
        }
        return Strings.EMPTY;
    }

    private boolean checkSignature(HttpServletRequest request) {
        String signature = request.getParameter("signature");
        // 在非生产环境中，签名为空时忽略签名校验
        if (signature == null && !"product".equals(this.profileSupplier.get())) {
            return true;
        }
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String[] array = { getToken(), timestamp, nonce };
        Arrays.sort(array);
        StringBuffer text = new StringBuffer();
        for (String s : array) {
            text.append(s);
        }
        return EncryptUtil.encryptBySha1(text).equals(signature);
    }

    private Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        try {
            SAXReader reader = new SAXReader();
            ServletInputStream in = request.getInputStream();
            if (in.available() > 0) {
                Document doc = reader.read(in);
                Element root = doc.getRootElement();
                List<Element> elements = root.elements();
                for (Element e : elements) {
                    parameters.put(e.getName(), e.getText());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parameters;
    }

    private WechatMessage getMessage(Map<String, String> parameters) {
        String messageType = parameters.get("MsgType");
        if (StringUtils.isNotBlank(messageType)) {
            messageType = messageType.toUpperCase();
            WechatMessageType type = EnumUtils.getEnum(WechatMessageType.class, messageType);
            if (type != null) {
                WechatMessage message = null;
                switch (type) {
                    case EVENT:
                        String event = parameters.get("Event");
                        if (StringUtils.isNotBlank(event)) {
                            WechatEventType eventType = EnumUtils.getEnum(WechatEventType.class,
                                    event.toUpperCase());
                            if (eventType != null) {
                                message = new WechatEventMessage(eventType);
                            }
                        }
                        break;
                    default:
                        ;
                }
                if (message != null) {
                    message.setId(MathUtil.parseLong(parameters.get("MsgId")));
                    message.setFromUsername(parameters.get("FromUserName"));
                    message.setToUsername(parameters.get("ToUserName"));
                    long createTime = MathUtil.parseLong(parameters.get("CreateTime"));
                    if (createTime > 0) {
                        message.setCreateTime(Instant.ofEpochMilli(createTime));
                    }
                }
                return message;
            }
        }
        return null;
    }

    private String toXml(WechatMessage message) {
        if (message instanceof WechatTextMessage) {
            WechatTextMessage tm = (WechatTextMessage) message;
            String xml = "<xml>";
            xml += "<ToUserName><![CDATA[" + tm.getToUsername() + "]]></ToUserName>";
            xml += "<FromUserName><![CDATA[" + tm.getFromUsername() + "]]></FromUserName>";
            xml += "<CreateTime>" + tm.getCreateTime().toEpochMilli() + "</CreateTime>";
            xml += "<MsgType><![CDATA[" + tm.getType().name().toLowerCase() + "]]></MsgType>";
            xml += "<Content><![CDATA[" + tm.getContent() + "]]></Content>";
            xml += "</xml>";
            return xml;
        }
        return Strings.EMPTY;
    }

    protected String getForwardUrl() {
        return null;
    }

    protected abstract String getToken();

}
