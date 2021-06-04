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
    // 扩展 webpack 配置，使 /components 加入编译
    chainWebpack: config => {
        const path = require('path');
        config.module
        .rule('js')
        .include
        .add(path.resolve(__dirname, './components'))
        .end()
        .use('babel')
        .loader('babel-loader')
        .tap(options => {
            return options
        });
    }
}
