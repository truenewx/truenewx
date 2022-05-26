<template>
    <el-dialog
        v-model="visible"
        destroy-on-close
        append-to-body
        :modal="options.modal"
        :close-on-click-modal="options['close-on-click-modal']"
        :close-on-press-escape="options['close-on-press-escape']"
        :show-close="options['show-close']"
        :center="options.center"
        :before-close="beforeClose"
        @closed="onClosed">
        <template #title>
            <div class="tnxel-dialog-title" :class="mergeClass({'border-bottom': title})" v-html="title"
                v-if="title || options['show-close']"></div>
        </template>
        <template v-if="$slots.default">
            <slot></slot>
        </template>
        <div :class="mergeClass()" v-html="contentValue" v-else-if="contentValue"></div>
        <tnxel-dialog-content :class="mergeClass()" ref="content" v-bind="contentProps" v-else></tnxel-dialog-content>
        <template #footer v-if="buttons && buttons.length">
            <div class="tnxel-dialog-footer" :class="mergeClass()">
                <el-button v-for="(button, index) in buttons" :type="button.type" :key="index"
                    @click="btnClick(index)">{{ button.caption || button.text }}
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script>
import $ from 'jquery';

const util = window.tnx.util;

export default {
    name: 'TnxelDialog',
    components: {
        'tnxel-dialog-content': null,
    },
    props: {
        modelValue: Boolean,
        container: String,
        title: String,
        content: String,
        contentProps: Object,
        buttons: Array,
        theme: String,
        width: {
            type: [Number, String],
            default: 512,
        },
    },
    emits: ['update:modelValue'],
    data() {
        return {
            visible: this.modelValue,
            contentValue: this.content,
            options: {
                modal: true, // 是否需要遮罩层
                'close-on-click-modal': false, // 是否可以通过点击遮罩层关闭对话框
                'close-on-press-escape': true, // 是否可以通过按下 ESC 关闭对话框
                'show-close': true, // 是否显示关闭按钮
                center: false, // 是否对头部和底部采用居中布局
                width: this.width,
                // 以上均为element的Dialog组件配置项
                onShown: undefined, // 对话框展示后的事件回调
                onClosed: undefined, // 对话框关闭后的事件回调
            },
            middleTop: '40vh',
            heightChangeObserver: null,
        };
    },
    computed: {
        dialogTop() {
            if (typeof this.options.top === 'function') {
                return this.options.top();
            } else if (this.options.top) {
                return this.options.top;
            } else {
                return this.middleTop;
            }
        },
        dialogWidth() {
            if (typeof this.options.width === 'function') {
                return this.options.width();
            } else if (typeof this.options.width === 'number') {
                return this.options.width + 'px';
            } else {
                return this.options.width;
            }
        },
    },
    watch: {
        modelValue(newValue, oldValue) {
            this.visible = this.modelValue;
            if (newValue && !oldValue) { // 从隐藏到显示
                let vm = this;
                this.$nextTick(function() {
                    vm.locate(true);
                });
            }
        },
        visible() {
            this.$emit('update:modelValue', this.visible);
        },
    },
    mounted() {
        let vm = this;
        this.$nextTick(function() {
            if (this.visible) {
                vm.locate(true);
            }

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
    methods: {
        mergeClass(classObject) {
            classObject = classObject || {};
            if (this.theme) {
                classObject['theme-' + this.theme] = true;
            }
            return classObject;
        },
        locate(observe) {
            let $dialog = undefined;
            if (this.container) {
                let $container = $(this.container);
                if ($container.length) {
                    $dialog = $container.next('.el-overlay').find('.el-dialog');
                }
            }
            if (!$dialog?.length) {
                $dialog = $('.el-dialog:last');
            }
            if ($dialog.length) {
                const height = $dialog.height();
                const docHeight = window.tnx.util.dom.getDocHeight();
                // 对话框高度占文档高度的比例
                const heightRatio = height / docHeight;
                // 为了获得更好的视觉舒适度，根据高度比确定对话框中线位置：从33vh->50vh
                const baseline = 33 + (50 - 33) * heightRatio;
                const baseTop = docHeight * baseline / 100;
                let top = (baseTop - height / 2);
                top = Math.max(top, 8); // 至少顶部留8px空隙
                this.middleTop = top + 'px';
                $dialog.css({
                    'margin-top': this.dialogTop,
                    'width': this.dialogWidth,
                    'max-height': 'calc(100vh - 16px)', // 最大高度时上下各留8px空隙
                });

                if (observe) {
                    this.heightChangeObserver = util.dom.observeHeightChange($dialog[0], this.locate);
                }
            }
        },
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
            if (this.heightChangeObserver) {
                this.heightChangeObserver.disconnect();
            }
        }
    }
}
</script>

<style>
tnxel-dialog {
    display: none;
}

.el-dialog {
    display: flex;
    flex-direction: column;
    margin-bottom: 0;
}

.el-dialog__header {
    padding: 0;
}

.el-dialog__header .el-dialog__headerbtn {
    margin-top: -0.5rem;
}

.tnxel-dialog-title {
    padding: 0.75rem 1rem;
    font-size: 1rem;
}

.tnxel-dialog-title > :last-child,
.el-dialog__body > div > :last-child {
    margin-bottom: 0;
}

.el-dialog__body {
    padding: 1rem;
    color: inherit;
    overflow-x: hidden;
    overflow-y: auto;
}

.el-dialog__footer {
    padding: 0;
}

.tnxel-dialog-footer {
    padding: 1rem;
    display: flex;
    flex-direction: row-reverse;
}

.el-dialog__footer .el-button {
    margin-left: 10px;
}
</style>
