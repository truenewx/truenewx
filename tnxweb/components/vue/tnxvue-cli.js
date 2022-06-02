// tnxvue-cli.js
const UglifyPlugin = require('uglifyjs-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
    uglify(config, options) {
        Object.assign(config, {
            optimization: {
                minimizer: [new UglifyPlugin({
                    uglifyOptions: Object.assign({
                        warnings: false,
                        compress: {
                            drop_console: false,
                            drop_debugger: true,
                        }
                    }, options)
                })]
            }
        });
    },
    copy(config, dependencies, libs, patterns) {
        let pluginPatterns = [];
        for (let lib of libs) {
            let from = lib.path;
            let to = 'libs/js/' + lib.name;
            if (config.mode === 'production') {
                let version = dependencies[lib.name];
                if (version) {
                    to += '-' + version;
                }
                from += lib.prod;
                to += lib.prod;
            }
            if (!from.endsWith('/')) {
                from += '.js';
            }
            if (!to.endsWith('/')) {
                to += '.js';
            }
            pluginPatterns.push({
                from: './node_modules/' + from,
                to: './' + to,
            });
            let globalVarName = config.externals[lib.name];
            if (globalVarName) {
                process.env['VUE_APP_LIBS_' + globalVarName] = process.env.VUE_APP_VIEW_BASE_URL + to;
            }
            if (lib.map) {
                pluginPatterns.push({
                    from: './node_modules/' + lib.path + lib.map,
                    to: './libs/js/',
                });
            }
        }
        if (patterns?.length) {
            pluginPatterns = pluginPatterns.concat(patterns);
        }
        config.plugins.push(new CopyWebpackPlugin(pluginPatterns));
    },
}
