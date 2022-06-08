// vue.config.js
module.exports = {
    publicPath: './',
    assetsDir: 'assets',
    devServer: {
        port: 8080
    },
    // 修改/src为/sample
    pages: {
        index: {
            entry: 'sample/main.js',
            template: 'public/index.html',
            filename: 'index.html'
        }
    },
}
