// tnxcore-fss.js
/**
 * 文件存储服务的客户端支持
 */
const fss = {
    getAppName() {
        return window.tnx.app.rpc.apps.fss ? 'fss' : undefined;
    },
    getBaseUrl() {
        let appName = this.getAppName();
        let rpc = window.tnx.app.rpc;
        return rpc.apps[appName] || rpc.getBaseUrl() + '/fss';
    },
    loadUploadLimit(type, callback) {
        window.tnx.app.rpc.get(this.getBaseUrl() + '/upload-limit/' + type, callback, {
            app: this.getAppName()
        });
    },
    getUploadUrl(type, scope) {
        let url = this.getBaseUrl() + '/upload/' + type;
        if (scope) {
            url += '/' + scope;
        }
        return url;
    }
};

export default fss;
