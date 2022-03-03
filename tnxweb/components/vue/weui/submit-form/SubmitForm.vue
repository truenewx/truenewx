<template>
    <div class="weui-form">
        <div class="weui-form__text-area" v-if="title || desc">
            <h2 class="weui-form__title" v-if="title">{{ title }}</h2>
            <div class="weui-form__desc" v-if="desc">{{ desc }}</div>
        </div>
        <div class="weui-form__control-area">
            <slot></slot>
        </div>
        <div class="weui-form__opr-area">
            <slot name="opr" v-if="$slots.opr"></slot>
            <template v-else>
                <tnxvw-button type="primary" @click="toSubmit" v-if="submit !== false">{{ _submitText }}</tnxvw-button>
                <tnxvw-button type="default" @click="toCancel" v-if="cancel !== false">{{ cancelText }}</tnxvw-button>
            </template>
        </div>
    </div>
</template>

<script>
import Button from '../button/Button';

export default {
    name: 'TnxvwSubmitForm',
    components: {
        'tnxvw-button': Button,
    },
    props: {
        title: String,
        desc: String,
        model: {
            type: Object,
            required: true,
        },
        submit: {
            type: [Function, Boolean],
            default: null,
        },
        submitText: String,
        cancel: {
            type: [String, Function, Boolean],
            default: true
        },
        cancelText: {
            type: String,
            default: '取消'
        },
    },
    data() {
        return {}
    },
    computed: {
        _submitText() {
            if (this.submitText) {
                return this.submitText;
            }
            return this.cancel === false ? '保存' : '提交';
        }
    },
    mounted() {
    },
    methods: {
        validate(callback, errorFocus) {
            // TODO 字段规则校验
            if (typeof callback === 'function') {
                callback(true);
            }
        },
        toSubmit(callback) {
            let vm = this;
            this.validate(function(success) {
                if (success) {
                    if (typeof callback !== 'function') {
                        callback = vm.submit;
                    }
                    if (typeof callback === 'function') {
                        callback(vm);
                    }
                }
            });
        },
        toCancel() {
            if (typeof this.cancel === 'function') {
                this.cancel();
            } else if (typeof this.cancel === 'string') {
                this.$router.back(this.cancel);
            } else if (this.cancel !== false) {
                this.$router.back();
            }
        },
    }
}
</script>

<style>
.weui-form {
    padding-top: 0;
}

.weui-form__title {
    margin-top: 1rem;
}

.weui-form__control-area {
    margin: 0 0 2rem 0;
    flex: unset; /* 使操作区尽量靠上而不是位于屏幕下端，以免表单填写时打开输入法遮盖了操作区，需关闭输入法后才能点击操作区的麻烦 */
}

.weui-form__text-area + .weui-form__control-area {
    margin-top: 2rem;
}
</style>
