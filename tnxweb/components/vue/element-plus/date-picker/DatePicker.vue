<template>
    <div class="d-flex" v-if="permanentable">
        <el-date-picker :type="type" v-model="model.value" :value-format="format" :editable="false"
            :placeholder="placeholder" :clearable="empty" :default-value="defaultDate" :picker-options="pickerOptions"
            :disabled="disabled || model.permanent" class="flex-grow-1" @change="onDateChange"/>
        <el-checkbox style="margin-left: 1rem; margin-right: 0.75rem;" v-model="model.permanent"
            @change="onPermanentChange">{{ permanentText }}
        </el-checkbox>
    </div>
    <el-date-picker :type="type" v-model="model" :value-format="format" :editable="false"
        :placeholder="placeholder" :clearable="empty" :default-value="defaultDate" :picker-options="pickerOptions"
        :disabled="disabled" v-else/>
</template>

<script>
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
    },
    emits: ['update:modelValue'],
    data() {
        let vm = this;
        return {
            permanentText: window.tnx.util.date.PERMANENT_DATE_TEXT,
            model: this.getModel(),
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
            return this.type === 'datetime' ? window.tnx.util.date.patterns.dateTime : window.tnx.util.date.patterns.date;
        },
        defaultDate() {
            if (this.defaultValue) {
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
        model(value) {
            this.$emit('update:modelValue', value);
        },
        modelValue() {
            this.model = this.getModel();
        }
    },
    methods: {
        onDateChange() {
            this.$emit('update:modelValue', this.model);
        },
        onPermanentChange() {
            // 如果不允许为空，需做特殊处理
            if (!this.empty) {
                if (!this.model.permanent && !this.model.value) { // 取消永久选项时日期值为空，则设置为默认值
                    this.model.value = this.defaultDateValue;
                }
                // 重新进行字段校验，以清除可能的字段校验错误消息
                let formItem = this.$parent;
                while (formItem && !formItem.elForm) {
                    formItem = formItem.$parent;
                }
                if (formItem && formItem.elForm && formItem.prop) {
                    formItem.elForm.validateField(formItem.prop);
                }
            }
            this.$emit('update:modelValue', this.model);
        },
        getModel() {
            let model = this.modelValue;
            if (this.permanentable) {
                model = model || {};
                // 可永久的日期不是对象，则封装为对象
                if (typeof model !== 'object') {
                    model = {
                        value: model
                    }
                }
                if (model.permanent) {
                    model.value = null;
                } else if (!this.empty && !model.value) {
                    model.value = this.defaultDateValue;
                }
            } else {
                if (model instanceof Date) {
                    return model.format(this.format);
                }
                if (typeof model === 'number') {
                    return new Date(model).format(this.format);
                }
            }
            return model;
        }
    }
}
</script>
