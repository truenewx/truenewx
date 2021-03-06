package org.truenewx.tnxjeex.payment.core.gateway.tenpay;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * 财付通xml工具类
 */
public class TenpayXmlUtil {

    // 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据
    public static Map<String, String> doXmlParse(String xml) throws JDOMException, IOException {
        if (null == xml || "".equals(xml)) {
            return null;
        }

        Map<String, String> m = new HashMap<>();
        InputStream in = HttpClientUtil.String2Inputstream(xml);
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List<?> list = root.getChildren();
        Iterator<?> it = list.iterator();
        while (it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List<?> children = e.getChildren();
            if (children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = TenpayXmlUtil.getChildrenText(children);
            }

            m.put(k, v);
        }

        // 关闭流
        in.close();

        return m;
    }

    // 获取子结点的xml
    public static String getChildrenText(List<?> children) {
        StringBuffer sb = new StringBuffer();
        if (!children.isEmpty()) {
            Iterator<?> it = children.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List<?> list = e.getChildren();
                sb.append("<" + name + ">");
                if (!list.isEmpty()) {
                    sb.append(TenpayXmlUtil.getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }

    // 获取xml编码字符集
    public static String getXmlEncoding(String xml) throws JDOMException, IOException {
        InputStream in = HttpClientUtil.String2Inputstream(xml);
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        in.close();
        return (String) doc.getProperty("encoding");
    }

    // 获取xml格式字符串
    public static String parseXmlString(Map<?, ?> xmlMap) {
        if (xmlMap != null && !xmlMap.isEmpty()) {
            StringBuilder xml = new StringBuilder("<xml>");
            for (Object key : xmlMap.keySet()) {
                Object value = xmlMap.get(key);
                xml.append("<").append(key.toString()).append(">");
                xml.append(value.toString());
                xml.append("</").append(key.toString()).append(">");
            }
            xml.append("</xml>");
            return xml.toString();
        }
        return "";
    }

}
