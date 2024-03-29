<template>
    <div class="tnxel-date-picker d-flex" v-if="permanentable">
        <el-date-picker :type="type" v-model="model.value" :value-format="format" :editable="editable"
            :placeholder="placeholderText" :clearable="empty" :default-value="defaultDate"
            :disabled-date="disabledDate" :disabled="disabled || model.permanent"
            :class="{'flex-grow-1': !pickerWidth}" :style="{width: pickerWidth}"
            @change="emitModelValue"/>
        <el-checkbox style="margin-left: 1rem; margin-right: 0.75rem;" v-model="model.permanent"
            @change="onPermanentChange">{{ permanentText }}
        </el-checkbox>
    </div>
    <el-date-picker class="tnxel-date-picker" :type="type" v-model="model.value" :value-format="format"
        :editable="editable" :placeholder="placeholderText" :clearable="empty" :default-value="defaultDate"
        :disabled-date="disabledDate" :disabled="disabled" :style="{width: pickerWidth}" v-else/>
</template>

<script>
import $ from 'jquery';

const util = window.tnx.util;

export default {
    name: 'TnxelDatePicker',
    props: {
        permanentable: Boolean,
        modelValue: [Date, Number, String, Object], // 仅permanentable为true时传入Object
        valueFormat: String,
        type: {
            type: String,
            default: 'date',
        },
        placeholder: {
            type: String,
            default: '请选择',
        },
        disabled: Boolean,
        empty: {
            type: Boolean,
            default: false,
        },
        defaultValue: [Date, Number, String],
        earliest: [Date, Number, String],
        latest: [Date, Number, String],
        pickerWidth: String,
        editable: {
            type: Boolean,
            default: true,
        },
    },
    emits: ['update:modelValue'],
    data() {
        let vm = this;
        let model = {
            value: null,
            permanent: false,
        }
        Object.assign(model, this.getModel());
        return {
            permanentText: util.date.PERMANENT_DATE_TEXT,
            model: model,
            disabledDate(date) {
                if (vm.earliest || vm.latest) {
                    date = new Date(date);
                    if (vm.earliest) {
                        let earliest = new Date(vm.earliest);
                        if (date.getTime() < earliest.getTime()) {
                            return true;
                        }
                    }
                    if (vm.latest) {
                        let latest = new Date(vm.latest);
                        if (date.getTime() > latest.getTime()) {
                            return true;
                        }
                    }
                }
                return false;
            },
        };
    },
    computed: {
        format() {
            let format;
            if (this.valueFormat) {
                format = this.valueFormat;
            } else {
                format = this.type === 'datetime' ? util.date.patterns.dateTime : util.date.patterns.date;
            }
            return window.tnx.date.toDayJsDateFormat(format);
        },
        defaultDate() {
            if (this.defaultValue) {
                if (this.defaultValue instanceof Date) {
                    return this.defaultValue;
                }
                return new Date(this.defaultValue);
            }
            return null;
        },
        defaultDateValue() {
            let date = this.defaultDate;
            return date ? date.formatDate() : null;
        },
        placeholderText() {
            if (this.placeholder) {
                return this.placeholder;
            }
            if (this.model.permanent) {
                return '';
            }
            return this.editable ? '格式：' + this.format : '请选择';
        },
    },
    watch: {
        modelValue() {
            let model = this.getModel();
            Object.assign(this.model, model);
        },
        defaultValue() {
            if (!this.empty && !this.model.value) {
                this.model.value = this.defaultDateValue;
            }
        },
    },
    mounted() {
        let vm = this;
        this.$watch('model', function() {
            vm.emitModelValue();
        }, {
            deep: true
        });
    },
    methods: {
        emitModelValue() {
            if (this.permanentable) {
                this.$emit('update:modelValue', this.model);
            } else {
                this.$emit('update:modelValue', this.model.value);
            }
            if (!this.empty) {
                // 重新进行字段校验，以清除可能的字段校验错误消息
                let formItem = this.$parent;
                while (formItem && !formItem.elForm) {
                    formItem = formItem.$parent;
                }
                if (formItem && formItem.elForm && formItem.prop) {
                    formItem.elForm.validateField(formItem.prop);
                } else { // 不隶属于表单栏目，则简单地移除错误框效果
                    let $element = $(this.$el);
                    if ($element.is('.el-input')) {
                        $element.removeClass('is-error');
                    } else {
                        $('.el-input', $element).removeClass('is-error');
                    }
                }
            }
        },
        onPermanentChange() {
            // 如果不允许为空，需做特殊处理
            if (!this.empty) {
                if (!this.model.permanent && !this.model.value) { // 取消永久选项时日期值为空，则设置为默认值
                    this.model.value = this.defaultDateValue;
                }
            }
            this.emitModelValue();
        },
        getModel() {
            let model = {
                value: null,
                permanent: false,
            };
            if (this.modelValue) {
                if (this.permanentable) {
                    if (typeof this.modelValue === 'object') {
                        model.value = this.modelValue.value;
                        model.permanent = this.modelValue.permanent;
                    } else {
                        model.value = this.modelValue;
                    }
                    if (model.permanent) {
                        model.value = null;
                    } else if (!this.empty && !model.value) {
                        model.value = this.defaultDateValue;
                    }
                } else {
                    if (this.modelValue instanceof Date) {
                        model.value = this.modelValue.format(this.format);
                    } else if (typeof this.modelValue === 'number') {
                        model.value = new Date(this.modelValue).format(this.format);
                    } else {
                        model.value = this.modelValue;
                    }
                }
            }
            return model;
        }
    }
}
</script>

<style>
.tnxel-date-picker.el-date-editor.el-input,
.tnxel-date-picker.el-date-editor.el-input__wrapper {
    width: 150px;
}
</style>
