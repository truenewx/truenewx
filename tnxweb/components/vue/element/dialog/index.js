/**
 * 基于ElementUI的对话框组件
 */
import $ from 'jquery';
import tnxvue from '../../tnxvue.js';
import componentDefinition from './Dialog.vue';

const Vue = tnxvue.libs.Vue;

export default function(content, title, buttons, options, contentProps) {
    let componentOptions = {};
    if (this.util.isComponent(content)) {
        componentOptions.components = {
            'tnxel-dialog-content': content
        };
        content = null;
    }
    let Dialog = Vue.extend(Object.assign({}, componentDefinition, componentOptions));

    const dialogId = 'dialog-' + (new Date().getTime());
    $('body').append('<div id="' + dialogId + '"></div>');
    if (!(buttons instanceof Array)) {
        buttons = [];
    }
    const dialog = new Dialog({
        propsData: {
            content: content,
            title: title,
            contentProps: contentProps,
            buttons: buttons,
            theme: options.theme,
        }
    }).$mount('#' + dialogId);
    dialog.options = Object.assign(dialog.options, options);
}
