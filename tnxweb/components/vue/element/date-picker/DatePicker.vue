<template>
    <div class="d-flex" v-if="permanentable">
        <el-date-picker :type="type" v-model="model.value" :value-format="format" :editable="false"
            :placeholder="placeholder" :clearable="empty" :default-value="defaultDate" :picker-options="pickerOptions"
            :disabled="disabled || model.permanent" :class="{'flex-grow-1': !pickerWidth}" :style="{width: pickerWidth}"
            @change="emitModelValue"/>
        <el-checkbox style="margin-left: 1rem; margin-right: 0.75rem;" v-model="model.permanent"
            @change="onPermanentChange">{{ permanentText }}
        </el-checkbox>
    </div>
    <el-date-picker :type="type" v-model="model.value" :value-format="format" :editable="false"
        :placeholder="placeholder" :clearable="empty" :default-value="defaultDate" :picker-options="pickerOptions"
        :disabled="disabled" :style="{width: pickerWidth}" v-else/>
</template>

<script>
import $ from 'jquery';

const util = window.tnx.util;

export default {
    name: 'TnxelDatePicker',
    props: {
        permanentable: Boolean,
        value: [Date, Number, String, Object], // 仅permanentable为true时传入Object
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
    },
    data() {
        let vm = this;
        let model = {
            value: null,
            permanent: false,
        }
        model = this.getModel();
        return {
            permanentText: util.date.PERMANENT_DATE_TEXT,
            model: model,
            pickerOptions: {
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
                }
            }
        };
    },
    computed: {
        format() {
            if (this.valueFormat) {
                return this.valueFormat;
            }
            return this.type === 'datetime' ? util.date.patterns.dateTime : util.date.patterns.date;
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
        }
    },
    watch: {
        value() {
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
                this.$emit('input', this.model);
            } else {
                this.$emit('input', this.model.value);
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
            if (this.value) {
                if (this.permanentable) {
                    if (typeof this.value === 'object') {
                        model.value = this.value.value;
                        model.permanent = this.value.permanent;
                    } else {
                        model.value = this.value;
                    }
                    if (model.permanent) {
                        model.value = null;
                    } else if (!this.empty && !model.value) {
                        model.value = this.defaultDateValue;
                    }
                } else {
                    if (this.value instanceof Date) {
                        model.value = this.value.format(this.format);
                    } else if (typeof this.value === 'number') {
                        model.value = new Date(this.value).format(this.format);
                    } else {
                        model.value = this.value;
                    }
                }
            }
            return model;
        }
    }
}
</script>
