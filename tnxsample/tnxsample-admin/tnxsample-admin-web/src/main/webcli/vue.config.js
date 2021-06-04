// vue.config.js
module.exports = {
    publicPath: process.env.VUE_APP_VIEW_BASE_URL,
    outputDir: '../webapp/static',
    assetsDir: 'libs',
    indexPath: 'index.html',
    devServer: {
        port: 8085
    },
    pages: {
        index: { // 前端开发模式下的页面
            entry: 'src/main.js',
            template: 'public/serve.html',
            filename: 'index.html', // 不能修改为其它值，否则页面无法正常打开，构建时被上面的indexPath覆盖
            minify: false, // 不压缩html代码
        },
        build: { // 前端构建后的页面
            entry: 'src/main.js',
            template: 'public/build.html',
            filename: 'index.jsp',
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
