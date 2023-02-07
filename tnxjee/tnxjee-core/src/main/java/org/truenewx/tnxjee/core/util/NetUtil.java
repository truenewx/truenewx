package org.truenewx.tnxjee.core.util;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.function.TrConsumer;
import org.truenewx.tnxjee.core.util.tuple.Binary;
import org.truenewx.tnxjee.core.util.tuple.Binate;

/**
 * 网络工具类
 *
 * @author jianglei
 */
// uri为资源的抽象语义，可相对也可绝对，可以不包含协议，类似于面向对象中的类，默认使用uri命名；
// url为资源的具体定位，是uri的实例，必须为包含协议的绝对地址，类似于面向对象中的对象，对于必须包含协议的绝对地址使用url命名。
public class NetUtil {

    public static final String LOCAL_HOST_NAME = "localhost";
    public static final String LOCAL_IP_V4 = "127.0.0.1";
    public static final String LOCAL_IP_V6 = "0:0:0:0:0:0:0:1";
    public static final String PROTOCOL_HTTP = "http://";
    public static final String PROTOCOL_HTTPS = "https://";

    private NetUtil() {
    }

    /**
     * 拼接URI
     *
     * @param contextUri 上下文URI
     * @param path       相对路径
     * @return 拼接后的完整URI
     */
    public static String concatUri(String contextUri, String path) {
        // 如果相对路径为空或/，则无需拼接
        if (StringUtils.isBlank(path) || Strings.SLASH.equals(path)) {
            return contextUri;
        }
        if (contextUri == null) {
            return null;
        }
        if (contextUri.endsWith(Strings.SLASH)) {
            contextUri = contextUri.substring(0, contextUri.length() - 1);
        }
        if (!path.startsWith(Strings.SLASH)) {
            path = Strings.SLASH + path;
        }
        return contextUri + path;
    }

    /**
     * 获取指定主机名（域名）对应的IP地址
     *
     * @param host 主机名（域名）
     * @return IP地址
     */
    public static String getIpByHost(String host) {
        if (StringUtil.isIp(host)) {
            return host;
        }
        StringBuilder s = new StringBuilder();
        try {
            InetAddress address = InetAddress.getByName(host);
            for (byte b : address.getAddress()) {
                s.append(b & 0xff).append(Strings.DOT);
            }
            if (s.length() > 0) {
                s = new StringBuilder(s.substring(0, s.length() - 1));
            }
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
        return s.toString();
    }

    public static List<String> getLocalIntranetIps() {
        List<String> ips = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    String ip = ias.nextElement().getHostAddress();
                    if (NetUtil.isIntranetIp(ip) && !LOCAL_IP_V4.equals(ip) && !LOCAL_IP_V6.equals(ip)) {
                        ips.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            LogUtil.error(NetUtil.class, e);
        }
        return ips;
    }

    /**
     * 获取本机网卡IP地址
     *
     * @return 本机网卡IP地址
     */
    public static String getLocalIp() {
        List<String> ips = getLocalIntranetIps();
        return ips.size() > 0 ? ips.get(0) : LOCAL_IP_V4;
    }

    /**
     * 获取指定ip地址字符串转换成的IPv4网络地址对象。如果无法转换则返回null
     *
     * @param ip ip地址字符串
     * @return IPv4网络地址对象
     */
    public static Inet4Address getInet4Address(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            if (address instanceof Inet4Address) {
                return (Inet4Address) address;
            }
        } catch (UnknownHostException ignored) {
        }
        return null;
    }

    public static String getTopDomain(String host) {
        if (StringUtil.isIp(host)) {
            return null;
        }
        String[] array = host.split("\\.");
        if (array.length >= 2) {
            return array[array.length - 2] + Strings.DOT + array[array.length - 1];
        }
        return host;
    }

    /**
     * 判断指定字符串是否内网IP地址
     *
     * @param s 字符串
     * @return true if 指定字符串是内网IP地址, otherwise false
     */
    public static boolean isIntranetIp(String s) {
        if (StringUtil.isIp(s)) {
            if (isLocalHost(s) || s.startsWith("192.168.") || s.startsWith("10.")) {
                return true;
            } else if (s.startsWith("172.")) { // 172.16-172.31网段
                String seg = s.substring(4, s.indexOf('.', 4)); // 取第二节
                int value = MathUtil.parseInt(seg);
                return 16 <= value && value <= 31;
            }
        }
        return false;
    }

    /**
     * 判断指定主机地址是否本机地址
     *
     * @param host 主机地址
     * @return 指定主机地址是否本机地址
     */
    public static boolean isLocalHost(String host) {
        return LOCAL_HOST_NAME.equals(host) || LOCAL_IP_V4.equals(host) || LOCAL_IP_V6.equals(host);
    }

    /**
     * 判断指定网络地址是否内网地址
     *
     * @param address 网络地址
     * @return 指定网络地址是否内网地址
     */
    public static boolean isIntranetAddress(InetAddress address) {
        byte[] b = address.getAddress();
        // 暂只考虑IPv4
        return b.length == 4 && ((b[0] == 192 && b[1] == 168) || b[0] == 10 || (b[0] == 172 && b[1] >= 16 && b[1] <= 31)
                || (b[0] == 127 && b[1] == 0 && b[2] == 0 && b[3] == 1));
    }

    /**
     * 获取指定IP地址的整数表达形式
     *
     * @param address IP地址
     * @return 整数表达形式
     */
    public static int intValueOf(InetAddress address) {
        // IPv4和IPv6的hashCode()即为其整数表达形式，本方法向调用者屏蔽该逻辑
        return address.hashCode();
    }

    /**
     * 将指定参数集合转换为参数字符串，形如: a=1&amp;b=true
     *
     * @param params   参数集合
     * @param encoding 字符编码
     * @return 参数字符串
     */
    @SuppressWarnings("unchecked")
    public static String map2ParamString(Map<String, Object> params, String encoding) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                if (value instanceof Collection) {
                    for (Object o : (Collection<Object>) value) {
                        result.append(key).append(Strings.EQUAL).append(encodeParam(o, encoding)).append(Strings.AND);
                    }
                } else if (value instanceof Object[]) {
                    for (Object o : (Object[]) value) {
                        result.append(key).append(Strings.EQUAL).append(encodeParam(o, encoding)).append(Strings.AND);
                    }
                } else {
                    result.append(key).append(Strings.EQUAL).append(encodeParam(value, encoding)).append(Strings.AND);
                }
            }
        }
        if (result.length() > 0) {
            result.delete(result.length() - Strings.AND.length(), result.length());
        }
        return result.toString();
    }

    private static String encodeParam(Object param, String encoding) {
        if (StringUtils.isNotBlank(encoding)) {
            try {
                return URLEncoder.encode(param.toString(), encoding);
            } catch (UnsupportedEncodingException e) {
            }
        }
        // 编码为空或不被支持，则不做编码转换
        return param.toString();
    }

    /**
     * 将指定参数字符串（形如: a=1&amp;b=true）转换为Map参数集合
     *
     * @param paramString 参数字符串
     * @return 参数集合
     */
    public static Map<String, Object> paramString2Map(String paramString) {
        Map<String, Object> map = new HashMap<>();
        String[] pairArray = paramString.split(Strings.AND);
        for (String pair : pairArray) {
            String[] entry = pair.split(Strings.EQUAL);
            if (entry.length == 2) {
                String key = entry[0];
                Object value = map.get(key);
                if (value instanceof Collection) {
                    @SuppressWarnings("unchecked")
                    Collection<Object> collection = (Collection<Object>) value;
                    collection.add(entry[1]);
                } else if (value != null) {
                    Collection<Object> collection = new ArrayList<>();
                    collection.add(entry[1]);
                    map.put(key, collection);
                } else {
                    map.put(key, entry[1]);
                }
            }
        }
        return map;
    }

    public static String mergeParam(String url, String paramName, Object paramValue) {
        if (paramName != null && paramValue != null) {
            if (url.contains(Strings.QUESTION)) {
                url += Strings.AND;
            } else {
                url += Strings.QUESTION;
            }
            url += paramName + Strings.EQUAL + paramValue;
        }
        return url;
    }

    /**
     * 将指定参数集合中的参数与指定URL中的参数合并，返回新的URL
     *
     * @param url      URL
     * @param params   参数集合
     * @param encoding 字符编码
     * @return 合并之后的新URL
     */
    public static String mergeParams(String url, Map<String, Object> params, String encoding) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        if (url.contains(Strings.QUESTION)) {
            url += Strings.AND;
        } else {
            url += Strings.QUESTION;
        }
        return url + map2ParamString(params, encoding);
    }

    private static String getQueryString(Map<String, Object> params) {
        String result = mergeParams(Strings.EMPTY, params, Strings.ENCODING_UTF8);
        if (result.length() > 0) {
            return result.substring(1); // 去掉首部问号
        }
        return Strings.EMPTY;
    }

    public static String removeParams(String url, Collection<String> paramNames) {
        if (paramNames.size() > 0) {
            int index = url.indexOf(Strings.QUESTION);
            if (index >= 0) {
                // 去参数字符串且以&开头和结尾，以便于处理
                String paramString = Strings.AND + url.substring(index + 1) + Strings.AND;
                for (String paramName : paramNames) {
                    String pattern = Strings.AND + paramName + Strings.EQUAL + "[^&=#?]+" + Strings.AND;
                    paramString = paramString.replaceAll(pattern, Strings.AND);
                }
                paramString = paramString.substring(1); // 去掉开头的&
                if (paramString.length() == 0) {
                    url = url.substring(0, index);
                } else {
                    url = url.substring(0, index + 1) + paramString;
                }
            } // 不包含参数的URL无需处理
        }
        return url;
    }

    /**
     * 下载指定URL和参数表示的资源到指定本地文件
     *
     * @param url       下载资源链接
     * @param params    下载资源链接的参数
     * @param localFile 本地文件
     * @throws IOException 如果下载过程中出现IO错误
     */
    public static void download(String url, Map<String, Object> params, File localFile) throws IOException {
        try {
            download(url, params, localFile, (length, in, out) -> {
                try {
                    IOUtils.copy(in, out);
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else {
                throw e;
            }
        }
    }

    public static void download(String url, Map<String, Object> params, File localFile,
            TrConsumer<Long, InputStream, OutputStream> consumer) throws IOException {
        url = mergeParams(url, params, Strings.ENCODING_UTF8);
        InputStream in = null;
        OutputStream out = null;
        try {
            IOUtil.createFile(localFile);
            out = new FileOutputStream(localFile);
            URL urlObj = new URL(url);
            URLConnection urlConnection = urlObj.openConnection();
            urlConnection.connect();
            long length = urlConnection.getContentLengthLong();
            in = urlConnection.getInputStream();
            consumer.accept(length, in, out);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else {
                throw e;
            }
        } finally {
            // 关闭时的异常不再向上层抛出
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LogUtil.error(NetUtil.class, e);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                LogUtil.error(NetUtil.class, e);
            }
        }
    }

    /**
     * 从后台向指定URL地址发送GET方式请求，获取响应结果
     *
     * @param url      URL地址
     * @param params   请求参数
     * @param encoding 参数值的字符集编码
     * @return 响应结果
     */
    public static String requestByGet(String url, Map<String, Object> params, String encoding) {
        if (StringUtils.isBlank(encoding)) {
            encoding = Strings.ENCODING_UTF8;
        }
        url = mergeParams(url, params, encoding);
        String result = Strings.EMPTY;
        InputStream in = null;
        try {
            URL urlObj = new URL(url);
            in = urlObj.openStream();
            result = IOUtils.toString(in, encoding);
        } catch (IOException e) {
            LogUtil.error(NetUtil.class, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LogUtil.error(NetUtil.class, e);
                }
            }
        }
        return result;
    }

    public static String requestByPost(String url, Map<String, Object> params, String encoding) {
        InputStream in = null;
        PrintWriter out = null;
        String response = "";
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            if (StringUtils.isBlank(encoding)) {
                encoding = Strings.ENCODING_UTF8;
            }
            connection.setRequestProperty("contentType", "text/html;charset=" + encoding);
            out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), encoding));
            out.write(getQueryString(params));
            out.flush();
            in = connection.getInputStream();
            byte[] b = new byte[in.available()];
            in.read(b);
            response = new String(b, encoding);
        } catch (IOException e) {
            LogUtil.error(NetUtil.class, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LogUtil.error(NetUtil.class, e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    LogUtil.error(NetUtil.class, e);
                }
            }
        }
        return response;
    }

    public static boolean isRelativeUri(String uri) {
        return uri.startsWith(Strings.SLASH) && !uri.startsWith(Strings.DOUBLE_SLASH);
    }

    /**
     * 标准化URI地址。所谓标准URI即：所有斜杠均为/，以/开头，不以/结尾
     *
     * @param uri URI
     * @return 标准化后的URI
     */
    public static String standardizeUri(String uri) {
        if (isRelativeUri(uri)) {
            uri = uri.replace('\\', '/');
            if (!uri.startsWith(Strings.SLASH)) {
                uri = Strings.SLASH + uri;
            }
            if (Strings.SLASH.equals(uri)) {
                return uri;
            }
        }
        if (uri.endsWith(Strings.SLASH)) {
            uri = uri.substring(0, uri.length() - 1);
        }
        return uri;
    }

    /**
     * 标准化指定URI中的协议，确保返回的URI包含协议，如果指定URI未包含协议，则返回包含有指定默认协议的URI
     *
     * @param uri             URI
     * @param defaultProtocol 默认协议，如："http"
     * @return 包含有协议的URI，如果输入的URL为相对路径，则原样返回
     */
    public static String standardizeUriWithProtocol(String uri, String defaultProtocol) {
        if (!uri.contains("://")) {
            if (uri.startsWith(Strings.DOUBLE_SLASH)) {
                uri = defaultProtocol + Strings.COLON + uri;
            } else if (!uri.startsWith(Strings.SLASH)) {
                uri = defaultProtocol + "://" + uri;
            }
            // 斜杠开头的为相对URL，不作处理
        }
        if (uri.endsWith(Strings.SLASH)) { // 确保不以斜杠结尾
            uri = uri.substring(0, uri.length() - 1);
        }
        return uri;
    }

    /**
     * 标准化指定URI，当该URI不包含协议时，返回包含HTTP协议的URI
     *
     * @param uri URI
     * @return 包含有协议（默认为HTTP协议）的URI
     */
    public static String standardizeHttpUri(String uri) {
        return standardizeUriWithProtocol(uri, "http");
    }

    /**
     * 从指定URI中截取请求action部分，即请求uri中去掉参数和请求后缀之后的部分
     *
     * @param uri 请求uri
     * @return 请求action
     */
    public static String getAction(String uri) {
        int questionIndex = uri.indexOf(Strings.QUESTION);
        if (questionIndex >= 0) {
            uri = uri.substring(0, questionIndex);
        }
        int dotIndex = uri.lastIndexOf(Strings.DOT);
        if (dotIndex >= 0) {
            int slashIndex = uri.lastIndexOf(Strings.SLASH);
            if (dotIndex > slashIndex) { // .在最后一个/之后，才是扩展名
                uri = uri.substring(0, dotIndex);
            }
        }
        return uri;
    }

    public static String getProtocol(String url, boolean withSlash) {
        int index = url.indexOf("://");
        if (index > 0) {
            if (withSlash) {
                index += 3;
            }
            return url.substring(0, index);
        }
        return null;
    }

    /**
     * 从指定URL地址中获取主机地址（域名/IP[:端口]）
     *
     * @param url          URL地址
     * @param portPossible 是否带上可能的端口号
     * @return 主机地址
     */
    public static String getHost(String url, boolean portPossible) {
        int index = url.indexOf("://");
        if (index >= 0) {
            url = url.substring(index + 3);
        } else if (url.startsWith(Strings.DOUBLE_SLASH)) { // 以//开头是不包含协议但包含主机地址的链接
            url = url.substring(2);
        } else { // 其它情况下URL中不包含主机地址
            return null;
        }
        index = url.indexOf(Strings.SLASH);
        if (index >= 0) {
            url = url.substring(0, index);
        }
        index = url.indexOf(Strings.COLON);
        if (index >= 0 && portPossible) { // 即使需要带上可能的端口号，但80和443端口必须忽略
            String portString = url.substring(index + 1);
            int port = MathUtil.parseInt(portString);
            if (port != 80 && port != 443) {
                index = -1;
            }
        }
        if (index >= 0) {
            url = url.substring(0, index);
        }
        return url;
    }

    public static String getSubDomain(String url, int topDomainLevel) {
        url = getHost(url, false);
        if (StringUtil.isIp(url)) {
            return null;
        }
        String[] domains = url.split("\\.");
        if (topDomainLevel < 2) {
            topDomainLevel = 2;
        }
        if (domains.length > topDomainLevel) {
            StringBuilder domain = new StringBuilder(domains[0]);
            for (int i = 1; i < domains.length - topDomainLevel; i++) {
                domain.append(Strings.DOT).append(domains[i]);
            }
            return domain.toString();
        }
        return null;
    }

    public static boolean isHttpUrl(String url, boolean acceptHttps) {
        if (url.startsWith(PROTOCOL_HTTP)) {
            return true;
        }
        if (acceptHttps && url.startsWith(PROTOCOL_HTTPS)) {
            return true;
        }
        return false;
    }

    public static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    public static String getContextUri(String uri, String contextPath) {
        if (StringUtils.isBlank(contextPath) || Strings.SLASH.equals(contextPath)) {
            if (isRelativeUri(uri)) { // 相对地址的上下文地址设为/
                return Strings.SLASH;
            }
            int index = uri.indexOf(Strings.DOUBLE_SLASH);
            if (index >= 0) {
                int fromIndex = index + Strings.DOUBLE_SLASH.length();
                index = uri.indexOf(Strings.SLASH, fromIndex);
                if (index < 0) {
                    index = uri.indexOf(Strings.WELL, fromIndex);
                }
                if (index < 0) {
                    index = uri.indexOf(Strings.QUESTION, fromIndex);
                }
                if (index > 0) {
                    return uri.substring(0, index);
                } else {
                    return uri;
                }
            }
        } else {
            int index = uri.indexOf(contextPath + Strings.SLASH);
            if (index < 0) {
                index = uri.indexOf(contextPath + Strings.WELL);
            }
            if (index < 0) {
                index = uri.indexOf(contextPath + Strings.QUESTION);
            }
            if (index >= 0) {
                return uri.substring(0, index + contextPath.length());
            } else if (uri.endsWith(contextPath)) {
                return uri;
            }
        }
        return null; // 无法解析出上下文地址
    }

    public static String getContextPathByContextUri(String contextUri) {
        int index = contextUri.indexOf(Strings.DOUBLE_SLASH);
        if (index >= 0) {
            index = contextUri.indexOf(Strings.SLASH, index + Strings.DOUBLE_SLASH.length());
            if (index > 0) {
                return contextUri.substring(index);
            } else {
                return Strings.SLASH;
            }
        }
        return null; // 无法解析出上下文根路径
    }

    /**
     * 判断指定端口是否已被占用
     *
     * @param port 端口号
     * @return 指定端口是否已被占用
     */
    public static boolean isOccupiedPort(int port) {
        try {
            InetAddress address = InetAddress.getByName(LOCAL_IP_V4);
            Socket socket = new Socket(address, port);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static int getPort(String url) {
        Binate<Integer, Integer> position = getPortPosition(url);
        int beginIndex = position.getLeft();
        int endIndex = position.getRight();
        String port = url.substring(beginIndex, endIndex);
        int defaultPort = url.startsWith(PROTOCOL_HTTPS) ? 443 : 80;
        return MathUtil.parseInt(port, defaultPort);
    }

    private static Binate<Integer, Integer> getPortPosition(String url) {
        int beginIndex = 0;
        // 跳过协议部分
        if (url.startsWith(Strings.DOUBLE_SLASH)) {
            beginIndex = Strings.DOUBLE_SLASH.length();
        } else {
            int index = url.indexOf("://");
            if (index >= 0) {
                beginIndex = index + 3;
            }
        }
        int colonIndex = url.indexOf(Strings.COLON, beginIndex);
        if (colonIndex > beginIndex) { // 包含冒号的，冒号后一位为起始位置
            beginIndex = colonIndex + 1;
        }
        int endIndex = url.indexOf(Strings.SLASH, beginIndex);
        if (endIndex < 0) { // 不包含/的，结束位置为末尾
            endIndex = url.length();
        }
        if (colonIndex < 0) { // 不含冒号的，起始位置和结束位置相同，表示url中不包含端口，位置为端口应该所在的起始位置
            beginIndex = endIndex;
        }
        return new Binary<>(beginIndex, endIndex);
    }

    public static String replacePort(String uri, int newPort) {
        Binate<Integer, Integer> position = getPortPosition(uri);
        int beginIndex = position.getLeft();
        int endIndex = position.getRight();
        String newUri = uri.substring(0, beginIndex);
        if (!newUri.endsWith(Strings.COLON)) {
            newUri += Strings.COLON;
        }
        newUri += newPort;
        if (0 <= endIndex && endIndex < uri.length()) {
            newUri += uri.substring(endIndex);
        }
        return newUri;
    }

}
