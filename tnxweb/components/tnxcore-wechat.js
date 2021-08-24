// tnxcore-wechat.js
/**
 * 微信登录客户端支持
 */
!function(a, b, c) {
    function d(a) {
        var c = "default";
        a.self_redirect === !0 ? c = "true" : a.self_redirect === !1 && (c = "false");
        var d = b.createElement("iframe"),
            e = window.location.protocol + "//open.weixin.qq.com/connect/qrconnect?appid=" + a.appid + "&scope=" + a.scope + "&redirect_uri=" + a.redirect_uri + "&state=" + a.state + "&login_type=jssdk&self_redirect=" + c + '&styletype=' + (a.styletype || '') + '&sizetype=' + (a.sizetype || '') + '&bgcolor=' + (a.bgcolor || '') + '&rst=' + (a.rst || '');
        e += a.style ? "&style=" + a.style : "", e += a.href ? "&href=" + a.href : "", d.src = e, d.frameBorder = "0", d.allowTransparency = "true", d.scrolling = "no", d.width = "300px", d.height = "400px";
        var f = b.getElementById(a.id);
        f.innerHTML = "", f.appendChild(d)
    }

    a.WxLogin = d
}(window, document);
// 以上来自于 http://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js

export const Wechat = function Wechat(appId, productContextUri) {
    this.appId = appId;
    this.productContextUri = productContextUri; // 微信接口要求必须为生产环境域名
}

function standardizeRedirectUri(redirectUri, productContextUri) {
    let protocol = window.location.protocol;
    let host = window.location.host;
    let uri = protocol + '//' + productContextUri;
    if (productContextUri.startsWith(host)) {
        uri += redirectUri;
    } else { // 不是生产环境则借助于生产环境的直接重定向能力进行再跳转
        uri += '/redirect/';
        if (redirectUri.startsWith('/')) { // 目标跳转地址是相对地址，则加上当前网站根地址
            uri += protocol.substr(0, protocol.length - 1) + '/' + host + redirectUri;
        } else { // 不以/开头，视为绝对地址
            let index = redirectUri.indexOf('://');
            if (index < 0) { // 绝对地址中一定包含://
                console.error('错误的跳转目标地址：' + redirectUri);
                return;
            }
            uri += redirectUri.substr(0, index); // 协议部分
            uri += redirectUri.substr(index + 2);
        }
    }
    return encodeURI(uri);
}

function standardizeState(state) {
    let stateString = '';
    if (typeof state === 'object') {
        stateString = JSON.stringify(state);
        stateString = window.tnx.util.base64.encode(stateString);
        if (stateString.length > 128) {
            delete state._next;
            stateString = JSON.stringify(state);
            stateString = window.tnx.util.base64.encode(stateString);
        }
    }
    return stateString;
}

Wechat.prototype.login = function(containerId, redirectUri, options) {
    options = options || {};
    if (options.cssHref && options.cssHref.startsWith('/')) {
        options.cssHref = window.location.protocol + '//' + window.location.host + options.cssHref;
    }

    new window.WxLogin({
        id: containerId,
        appid: this.appId,
        scope: "snsapi_login",
        redirect_uri: standardizeRedirectUri(redirectUri, this.productContextUri),
        href: options.cssHref,
        state: standardizeState(options.state),
    });
};

Wechat.prototype.authorize = function(redirectUri, state, silent) {
    // 请求参数有严格的顺序要求，不能更改参数顺序
    let url = window.location.protocol + '//open.weixin.qq.com/connect/oauth2/authorize?appid=' + this.appId;
    url += '&redirect_uri=' + standardizeRedirectUri(redirectUri, this.productContextUri);
    url += '&response_type=code';
    url += '&scope=' + (silent ? 'snsapi_base' : 'snsapi_userinfo');
    state = standardizeState(state);
    if (state) {
        url += '&state=' + state;
    }
    window.location.href = url;
}

export default Wechat;
