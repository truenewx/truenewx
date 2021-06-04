// main.js
require.config({
    paths: {
        "app": app_config.path + "/assets/js/app.js?v=" + app_config.version,
    }
});

require(["src/main/webapp/public/assets/js/app"], function(app) {
    app.init(function() {
        console.info("App onload.");
    });
});
