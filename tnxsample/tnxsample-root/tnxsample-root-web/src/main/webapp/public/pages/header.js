// header.js
define([], function() {
    return function(app) {
        var container = $("header")[0];
        new Vue({
            el: container,
            data: {},
            methods: {
                toUpdateInfo: function() {
                    // tnx.open(app.context + "/mine/info.win");
                }
            }
        });
    }
});
