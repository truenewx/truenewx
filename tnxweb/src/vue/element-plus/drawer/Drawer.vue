<template>
    <el-drawer
        v-model="visible"
        destroy-on-close
        append-to-body
        :size="options.width"
        :modal="options.modal"
        :close-on-click-modal="options['close-on-click-modal']"
        :close-on-press-escape="options['close-on-press-escape']"
        :show-close="options['show-close']"
        :center="options.center"
        :before-close="beforeClose"
        :with-header="(title || options['show-close']) ? true : false"
        @closed="onClosed">
        <template #title>
            <div class="tnxel-drawer-title" :class="themeClass" v-html="title"></div>
        </template>
        <div class="tnxel-drawer-main" :class="themeClass">
            <div class="tnxel-drawer-content">
                <template v-html="contentValue" v-if="contentValue"/>
                <tnxel-drawer-content ref="content" v-bind="contentProps" v-else>
                </tnxel-drawer-content>
            </div>
            <div class="tnxel-drawer-footer" v-if="buttons && buttons.length">
                <el-button v-for="(button, index) in buttons" :type="button.type" :key="index"
                    @click="btnClick(index)">{{ button.caption || button.text }}
                </el-button>
            </div>
        </div>
    </el-drawer>
</template>

<script>
const util = window.tnx.util;

export default {
    name: 'TnxelDrawer',
    components: {
        'tnxel-drawer-content': null,
    },
    props: {
        title: String,
        content: String,
        contentProps: Object,
        buttons: Array,
        theme: String,
    },
    data() {
        return {
            visible: true,
            contentValue: this.content,
            options: {
                modal: true, // 是否需要遮罩层
                'close-on-click-modal': false, // 是否可以通过点击遮罩层关闭对话框
                'close-on-press-escape': true, // 是否可以通过按下 ESC 关闭对话框
                'show-close': true, // 是否显示关闭按钮
                center: false, // 是否对头部和底部采用居中布局
                // 以上均为element的Drawer组件配置项
                width: undefined,
                onShown: undefined, // 对话框展示后的事件回调
                onClosed: undefined, // 对话框关闭后的事件回调
            },
        };
    },
    mounted() {
        let vm = this;
        this.$nextTick(function() {
            if (vm.$refs.content && !vm.$refs.content.close) {
                vm.$refs.content.close = function() {
                    vm.close();
                }
            }
            if (typeof vm.options.onShown === 'function') {
                vm.options.onShown.call(this);
            }
        });
    },
    computed: {
        themeClass() {
            let classObject = {};
            if (this.theme) {
                classObject['theme-' + this.theme] = true;
            }
            return classObject;
        },
    },
    methods: {
        btnClick(index) {
            const button = this.buttons[index];
            if (button && typeof button.click === 'function') {
                if (button.click.call(this.$refs.content, this.close) === false) {
                    return;
                }
            }
            this.close();
        },
        close(callback) {
            const vm = this;
            this.beforeClose(function() {
                if (typeof callback === 'function') {
                    vm.options.onClosed = util.function.around(vm.options.onClosed, function(onClosed) {
                        if (onClosed) {
                            onClosed();
                        }
                        callback();
                    });
                }
                vm.visible = false;
            });
        },
        beforeClose(done) {
            if (typeof this.options.beforeClose === 'function') {
                if (!this.options.beforeClose()) {
                    return;
                }
            }
            done();
        },
        onClosed() {
            if (typeof this.options.onClosed === 'function') {
                this.options.onClosed.call(this.$refs.content);
            }
        }
    }
}
</script>

<style>
.el-drawer__header {
    padding: 0;
    margin-bottom: 0;
    color: unset;
}

.el-drawer__header .el-drawer__close-btn {
    margin-top: -0.5rem;
    margin-right: 0.5rem;
    color: var(--el-text-color-secondary);
}

.tnxel-drawer-title {
    padding: 0.75rem 1rem;
    font-size: 1rem;
}

.el-drawer__body {
    padding: 0;
    flex-grow: 1;
    max-height: calc(100vh - 60px);
}

.tnxel-drawer-main {
    height: 100%;
    display: flex;
    flex-direction: column;
}

.tnxel-drawer-main .tnxel-drawer-content {
    flex-grow: 1;
    padding: 1rem 1rem 0 1rem;
    overflow: auto;
}

.tnxel-drawer-footer {
    padding: 1rem;
    display: flex;
    flex-direction: row-reverse;
    justify-content: start;
}

.tnxel-drawer-footer .el-button {
    margin-left: 0;
    margin-right: 0.5rem;
}
</style>
