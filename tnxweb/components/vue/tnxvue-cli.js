// tnxvue-cli.js
module.exports = {
    buildCopyPluginPatterns(config, dependencies, libs) {
        let copyPluginPatterns = [];
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
            copyPluginPatterns.push({
                from: './node_modules/' + from,
                to: './' + to,
            });
            let globalVarName = config.externals[lib.name];
            if (globalVarName) {
                process.env['VUE_APP_LIBS_' + globalVarName] = process.env.VUE_APP_VIEW_BASE_URL + to;
            }
            if (lib.map) {
                copyPluginPatterns.push({
                    from: './node_modules/' + lib.path + lib.map,
                    to: './libs/js/',
                });
            }
        }
        return copyPluginPatterns;
    }
}
