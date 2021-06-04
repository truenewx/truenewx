// app.js
import tnxel from '../components/vue/element/tnxel';

export const tnx = tnxel;
export const util = tnx.util;
export const app = tnx.app;

app.rpc.setBaseUrl(process.env.VUE_APP_RPC_BASE_URL);

app.rpc.toLogin = function(loginFormUrl, originalUrl) {
    if (loginFormUrl) {
        let alertable = originalUrl !== undefined;
        const username = process.env.VUE_APP_LOGIN_USERNAME;
        const password = process.env.VUE_APP_LOGIN_PASSWORD;
        if (username && password) { // 将默认用户名密码插入到参数清单头部，以免被其它参数中的#影响而被忽略
            const index = loginFormUrl.indexOf('?') + 1;
            loginFormUrl = loginFormUrl.substr(0, index) + 'username=' + username + '&password='
                + password + '&' + loginFormUrl.substr(index);
            alertable = false;
        }
        if (alertable) {
            tnx.alert('尚未登录或登录会话已过期，需重新登录', function() {
                window.location.href = loginFormUrl;
            });
        } else {
            window.location.href = loginFormUrl;
        }
        return true;
    }
    return false;
}

export default app;
