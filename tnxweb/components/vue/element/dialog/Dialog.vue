<template>
    <el-dialog
        :class="this.theme ? ('theme-' + this.theme) : ''"
        :visible.sync="visible"
        :destroy-on-close="true"
        :append-to-body="true"
        :modal="options.modal"
        :close-on-click-modal="options['close-on-click-modal']"
        :close-on-press-escape="options['close-on-press-escape']"
        :show-close="options['show-close']"
        :center="options.center"
        :width="width"
        :top="top"
        :before-close="beforeClose"
        @closed="onClosed">
        <div slot="title" class="dialog-title" :class="{'border-bottom': title}" v-html="title"></div>
        <div v-if="contentValue" v-html="contentValue"></div>
        <tnxel-dialog-content ref="content" v-bind="contentProps" v-else></tnxel-dialog-content>
        <div slot="footer" class="dialog-footer" :class="{'border-top': buttons && buttons.length}">
            <el-button v-for="(button, index) in buttons" :type="button.type" :key="index"
                @click="btnClick(index)">{{ button.caption || button.text }}
            </el-button>
        </div>
    </el-dialog>
</template>

<script>
import $ from 'jquery';

export default {
    name: 'TnxelDialog',
    props: ['title', 'content', 'contentProps', 'buttons', 'theme'],
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
                width: '512px',
                // 以上均为element的Dialog组件配置项
                onShown: undefined, // 对话框展示后的事件回调
            },
            middleTop: '40vh',
        };
    },
    computed: {
        top() {
            if (typeof this.options.top === 'function') {
                return this.options.top();
            } else if (this.options.top) {
                return this.options.top;
            } else {
                return this.middleTop;
            }
        },
        width() {
            if (typeof this.options.width === 'function') {
                return this.options.width();
            } else {
                return this.options.width;
            }
        },
    },
    mounted() {
        this.$nextTick(function() {
            const height = $('.el-dialog:last').height();
            const docHeight = window.tnx.util.dom.getDocHeight();
            // 对话框高度占文档高度的比例
            const heightRatio = height / docHeight;
            // 为了获得更好的视觉舒适度，根据高度比确定对话框中线位置：从33vh->50vh
            const baseline = 33 + (50 - 33) * heightRatio;
            const baseTop = docHeight * baseline / 100;
            let top = (baseTop - height / 2);
            top = Math.max(top, 5); // 至少顶部留5px空隙
            this.middleTop = top + 'px';

            if (typeof this.options.onShown === 'function') {
                this.options.onShown.call(this);
            }
        });
    },
    components: {
        'tnxel-dialog-content': null,
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
        close() {
            const vm = this;
            this.beforeClose(function() {
                vm.visible = false;
            });
        },
        beforeClose(done) {
            if (typeof this.options.beforeClose === 'function') {
                if (!this.options.beforeClose()) {
                    return;
                }
            }
            // 避免组件内容在关闭时被再次加载，并出现闪现现象
            const height = $('.el-dialog__wrapper:last .el-dialog__body').height();
            this.contentValue = '<div style="height: ' + height + 'px"></div>';
            done();
        },
        onClosed() {
            $('.el-dialog__wrapper:last').remove();
            if (typeof this.options.onClosed === 'function') {
                this.options.onClosed.call(this.$refs.content);
            }
        }
    }
}
</script>

<style>
.el-dialog__header {
    padding: 0;
}

.dialog-title {
    font-size: 16px; /* 与关闭按钮大小一致 */
    padding: 1rem;
}

.dialog-title > :last-child,
.el-dialog__body > div > :last-child {
    margin-bottom: 0;
}

.el-dialog__body {
    padding: 1rem;
    color: inherit;
}

.el-dialog__footer {
    padding: 0;
}

.dialog-footer {
    padding: 1rem;
    display: flex;
    flex-direction: row-reverse;
}

.dialog-footer .el-button {
    margin-left: 10px;
}

.el-message-box {
    width: unset;
    min-width: 256px;
    max-width: 512px;
    word-wrap: break-word;
    word-break: normal;
}
</style>
