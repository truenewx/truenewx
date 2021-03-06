// tnxcore-fss.js
/**
 * 文件存储服务的客户端支持<br>
 * 文件存储服务的部署方式有三种：
 * - 独立应用：作为独立部署的微服务向其它应用提供服务，具有标准的RPC客户端
 * - 子应用：嵌入在某个应用中，作为虚拟的应用向其它应用提供服务，具有引用型的RPC客户端，其内部ref属性指向所属应用的RPC客户端
 * - 非应用：嵌入在某个应用中，且不向其它应用提供服务，适用于系统只包含一个应用的简单场景，没有专属的RPC客户端
 */
const fss = {
    PROTOCOL: 'fss://',
    appName: undefined,
    getClientConfig() {
        const rpc = window.tnx.app.rpc;
        // 默认配置为非应用部署时的配置
        let config = {
            appName: this.appName,
            baseUrl: rpc.getDefaultBaseUrl(),
            contextUrl: '/fss',
        }
        if (this.appName) {
            let client = rpc.appClients[this.appName];
            if (client) {
                config.appName = this.appName;
                if (client.ref) { // 子应用
                    config.baseUrl = client.ref.baseUrl;
                } else { // 独立应用
                    config.baseUrl = client.baseUrl;
                }
                config.contextUrl = '';
            }
        }
        return config;
    },
    loadUploadOptions(type, params, callback) {
        if (typeof params === 'function') {
            callback = params;
            params = undefined;
        }
        let config = this.getClientConfig();
        window.tnx.app.rpc.get(config.contextUrl + '/upload-options/' + type, params, callback, {
            app: config.appName
        });
    },
    getUploadUrl(type, scope) {
        let config = this.getClientConfig();
        let url = config.baseUrl + config.contextUrl + '/upload/' + type;
        if (scope !== undefined && scope !== null) {
            url += '/' + scope;
        }
        return url;
    },
    upload(type, scope, file, callback) {
        let formData = new FormData();
        formData.append('file', file);
        let url = this.getUploadUrl(type, scope);
        window.tnx.app.rpc.post(url, formData, function(meta) {
            callback(meta);
        }, {
            headers: {'Content-Type': 'multipart/form-data'},
            app: this.getClientConfig().appName,
        });
    },
    resolveDownloadUrl(locationUrl, callback) {
        let config = this.getClientConfig();
        window.tnx.app.rpc.get(config.contextUrl + '/download/url', {locationUrl}, callback, {
            app: config.appName,
        });
    },
    readText(locationUrl, callback) {
        let config = this.getClientConfig();
        window.tnx.app.rpc.get(config.contextUrl + '/read/text', {locationUrl}, callback, {
            app: config.appName,
        });
    }
};

export default fss;
