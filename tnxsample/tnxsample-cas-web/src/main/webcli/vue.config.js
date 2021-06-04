// vue.config.js
module.exports = {
    publicPath: '/cas',
    outputDir: '../webapp/static',
    assetsDir: 'libs',
    devServer: {
        port: 8083
    },
    pages: {
        index: {
            entry: 'src/libs.js',
            template: 'public/libs.html',
            filename: 'libs.jsp',
            minify: false, // 不压缩html代码
        }
    },
    configureWebpack: {
        externals: {
            'jquery': 'jQuery',
            'vue': 'Vue',
            'vue-router': 'VueRouter',
            'element-ui': 'ELEMENT',
        }
    }
}
