<template>
    <div class="d-flex" v-if="permanentable">
        <el-date-picker :type="type" v-model="model.value" :value-format="format" :editable="false"
            :placeholder="placeholder" :clearable="empty" :default-value="defaultDate" :picker-options="pickerOptions"
            :disabled="disabled || model.permanent" class="flex-grow-1"/>
        <el-checkbox style="margin-left: 1rem; margin-right: 0.75rem;" v-model="model.permanent">长期</el-checkbox>
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
    },
    data() {
        let vm = this;
        return {
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
    },
    watch: {
        model(value) {
            this.$emit('input', value);
        },
        value() {
            this.model = this.getModel();
        }
    },
    methods: {
        getModel() {
            if (this.permanentable) {
                this.value = this.value || {};
                // 可永久的日期不是对象，则封装为对象
                if (typeof this.value !== 'object') {
                    this.value = {
                        value: this.value
                    }
                }
                if (this.value.permanent) {
                    this.value.value = null;
                }
            } else {
                if (this.value instanceof Date) {
                    return this.value.format(this.format);
                }
                if (typeof this.value === 'number') {
                    return new Date(this.value).format(this.format);
                }
            }
            return this.value;
        }
    }
}
</script>
