<template>
    <el-form ref="form" :id="id" :label-width="labelWidth" :label-position="vertical ? 'top' : 'right'"
        :model="model" :rules="validationRules" :validate-on-rule-change="false"
        :inline="inline" :inline-message="!vertical" :disabled="disabled"
        :class="theme ? ('theme-' + theme) : null" :size="size" :status-icon="statusIcon">
        <slot></slot>
        <el-form-item class="w-100 mb-0" v-if="submit !== undefined && submit !== null">
            <el-button :type="theme || 'primary'" :size="size" @click="toSubmit" v-if="submit !== false">
                {{ _submitText }}
            </el-button>
            <el-button :size="size" @click="toCancel" v-if="cancel !== false">{{ cancelText }}</el-button>
        </el-form-item>
    </el-form>
</template>

<script>
import $ from 'jquery';
import AsyncValidator from 'async-validator';

export default {
    name: 'TnxelSubmitForm',
    props: {
        /**
         * 所属滚动容器的选择器
         */
        container: {
            type: String,
            default: 'main',
        },
        model: {
            type: Object,
            required: true,
        },
        rules: [String, Object], // 加载字段校验规则的URL地址，或规则集对象
        rulesApp: String, // 加载字段校验规则的应用名称
        rulesLoaded: Function, // 规则集加载后的附加处理函数，仅在rules为字符串类型的URL地址时有效
        submit: {
            type: [Function, Boolean],
            default: null,
        },
        submitText: String,
        theme: String,
        cancel: {
            type: [String, Function, Boolean],
            default: true
        },
        cancelText: {
            type: String,
            default: '取消'
        },
        vertical: {
            type: Boolean,
            default: false
        },
        inline: {
            type: Boolean,
            default: false
        },
        errorFocus: {
            type: Boolean,
            default: false,
        },
        labelWidth: {
            type: [String, Number],
            default: 'auto',
        },
        size: String,
        statusIcon: {
            type: Boolean,
            default: true,
        },
    },
    emits: ['rules-loaded', 'meta'],
    data() {
        return {
            id: window.tnx.util.string.random(32),
            validationRules: {},
            disabled: false,
            topOffset: 0,
            fieldNames: [],
        };
    },
    computed: {
        _submitText() {
            if (this.submitText) {
                return this.submitText;
            }
            return this.cancel === false ? '保存' : '提交';
        }
    },
    created() {
        if (typeof this.rules === 'string') {
            const vm = this;
            window.tnx.app.rpc.getMeta(this.rules, meta => {
                if (vm.rulesLoaded) {
                    vm.rulesLoaded(meta.$rules);
                } else {
                    vm.$emit('rules-loaded', meta.$rules);
                }
                vm.validationRules = meta.$rules;
                delete meta.$rules;
                this.$emit('meta', meta);
                vm.fieldNames = Object.keys(meta);
            }, this.rulesApp);
        } else if (this.rules) {
            this.validationRules = this.rules;
        }
    },
    mounted() {
        let vm = this;
        this.$nextTick(function() {
            let container = $(vm.container);
            if (container.length) {
                vm.topOffset = $('#' + vm.id).offset().top - container.offset().top - 16;
            }
        });
    },
    methods: {
        disable(disabled) {
            this.disabled = disabled !== false;
        },
        focusError(errors) {
            let $form = $('#' + this.id);
            let fieldNames = Object.keys(errors);
            let fieldName = fieldNames[0];
            if (fieldName) {
                let $item = $('.el-form-item label[for=' + fieldName + ']', $form).parents('.el-form-item');
                if ($item.length) {
                    let $input = $('input:first', $item);
                    if ($input.length) {
                        $input.focus();
                        return;
                    }
                }
                // 没有找到错误字段输入框，则滚动到错误栏目处
                this.$refs.form.scrollToField(fieldName);
            }
        },
        validate(callback, errorFocus) {
            let _this = this;
            this.$refs.form.validate().then(function() {
                if (typeof callback === 'function') {
                    callback(true);
                }
            }).catch(function(errors) {
                if (_this.errorFocus && errorFocus !== false) {
                    _this.$nextTick(function() {
                        _this.focusError.call(_this, errors);
                    });
                }
                if (typeof callback === 'function') {
                    callback(false);
                }
            });
        },
        validateField(props, callback, errorFocus) {
            let _this = this;
            this.$refs.form.validateField(props, function(errorMessage) {
                if (errorMessage && _this.errorFocus && errorFocus !== false) {
                    _this.$nextTick(function() {
                        _this.focusError.call(_this);
                    });
                }
                if (typeof callback === 'function') {
                    callback(errorMessage);
                }
            });
        },
        clearValidate(props) {
            this.$refs.form.clearValidate(props);
        },
        toSubmit(callback, disabled) {
            const vm = this;
            this.validate(function(success) {
                if (success) {
                    if (typeof callback !== 'function') {
                        callback = vm.submit;
                    }
                    if (typeof callback === 'function') {
                        if (disabled !== false) {
                            vm.disable();
                        }
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
        getFieldNames() {
            return this.fieldNames;
        },
        /**
         * 执行所有的校验规则，以自定义方式处理错误
         * @param callback 校验后的回调函数，首个参数为错误消息字符串数组，没有错误时为null
         */
        validateRules(callback, fieldLabels) {
            let validator = new AsyncValidator(this.validationRules);
            validator.validate(this.model, function(errors) {
                let messages = [];
                if (errors) {
                    for (let error of errors) {
                        let fieldLabel = undefined;
                        if (typeof fieldLabels === 'function') {
                            fieldLabel = fieldLabels(error.field);
                        } else if (typeof fieldLabels === 'object') {
                            fieldLabel = fieldLabels[error.field];
                        }
                        fieldLabel = fieldLabel || error.field;
                        messages.push(fieldLabel + error.message);
                    }
                }
                if (messages.length === 0) {
                    messages = null;
                }
                callback(messages);
            });
        }
    }
}
</script>
