<template>
    <el-form :id="id" :label-position="vertical ? 'top' : 'right'" label-width="auto" ref="form" :model="model"
        :rules="validationRules" :validate-on-rule-change="false" :inline-message="!vertical"
        :disabled="disabled" :class="theme ? ('theme-' + theme) : null" status-icon>
        <slot></slot>
        <el-form-item class="w-100" v-if="submit !== undefined && submit !== null">
            <el-button :type="theme || 'primary'" @click="toSubmit" v-if="submit !== false">
                {{ _submitText }}
            </el-button>
            <el-button type="default" @click="toCancel" v-if="cancel !== false">{{ cancelText }}</el-button>
        </el-form-item>
    </el-form>
</template>

<script>
import $ from 'jquery';

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
        rulesLoaded: Function, // 规则集加载后的附加处理函数，仅在rule为字符串类型的URL地址时有效
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
        errorFocus: {
            type: Boolean,
            default: false,
        }
    },
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
        this.topOffset = $('#' + this.id).offset().top - $(this.container).offset().top - 16;
    },
    methods: {
        disable(disabled) {
            this.disabled = disabled !== false;
        },
        focusError() {
            let $form = $('#' + this.id);
            let $item = $('.el-form-item.is-error:first', $form);
            if ($item.length) {
                let top = $item.offset().top - $form.offset().top + this.topOffset;
                $(this.container).scrollTop(top);
            }
        },
        validate(callback, errorFocus) {
            let _this = this;
            return this.$refs.form.validate(function(valid, invalidFields) {
                if (!valid && _this.errorFocus && errorFocus !== false) {
                    _this.$nextTick(function() {
                        _this.focusError.call(_this);
                    });
                }
                if (typeof callback === 'function') {
                    callback(valid);
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
        }
    }
}
</script>
