<template>
    <el-row class="tnxel-datetime-picker" :gutter="8">
        <el-col :span="12">
            <el-date-picker ref="datePicker"
                v-model="model"
                :format="convertedDateFormat"
                :default-value="defaultValue"
                :placeholder="datePlaceholder"
                :size="size"
                :clearable="empty"
                :disabled-date="isDisabledDate"
                :disabled="disabled"/>
        </el-col>
        <el-col :span="12">
            <el-time-picker ref="timePicker"
                v-model="model"
                :format="timeFormat"
                :default-value="defaultTimeValue"
                :placeholder="timePlaceholder"
                :size="size"
                :clearable="empty"
                :disabled="disabled || !model"/>
        </el-col>
    </el-row>
</template>

<script>
export default {
    name: "TnxelDateTimePicker",
    props: {
        modelValue: [String, Number, Date],
        dateFormat: {
            type: String,
            default: 'yyyy-MM-dd',
        },
        datePlaceholder: {
            type: String,
            default: '选择日期',
        },
        timeFormat: {
            type: String,
            default: 'HH:mm', // 默认精确到分钟
        },
        timePlaceholder: {
            type: String,
            default: '选择时间',
        },
        delimiter: {
            type: String,
            default: ' ', // 默认用空格分隔日期和时间
        },
        size: String,
        empty: {
            type: Boolean,
            default: false,
        },
        defaultValue: [String, Number, Date],
        valueFormat: String,
        disabled: Boolean,
        minDate: [String, Number, Date],
        maxDate: [String, Number, Date],
    },
    emits: ['update:modelValue'],
    data() {
        return {
            model: window.tnx.util.date.toDate(this.modelValue),
        }
    },
    computed: {
        convertedDateFormat() {
            let format = this.dateFormat.replaceAll('y', 'Y');
            return format.replaceAll('d', 'D');
        },
        defaultTimeValue() {
            if (this.defaultValue) {
                return window.tnx.util.date.toDate(this.defaultValue);
            }
            // 必须创建新的日期对象，以免改动影响原始modelValue
            let time = this.modelValue ? new Date(this.modelValue) : new Date();
            time.applyTime(0, 0, 0, 0);
            return time;
        },
    },
    watch: {
        modelValue() {
            this.model = window.tnx.util.date.toDate(this.modelValue);
        },
        model() {
            this.$emit('update:modelValue', this.model);
        },
    },
    methods: {
        isDisabledDate(date) {
            let minDate = window.tnx.util.date.toDate(this.minDate);
            if (minDate) {
                minDate.applyTime(0, 0, 0, 0);
                if (minDate.getTime() > date.getTime()) {
                    return true;
                }
            }
            let maxDate = window.tnx.util.date.toDate(this.maxDate);
            if (maxDate) {
                maxDate.applyTime(0, 0, 0, 0);
                if (maxDate.getTime() < date.getTime()) {
                    return true;
                }
            }
            return false;
        },
    },
}
</script>

<style>
.tnxel-datetime-picker .el-input {
    width: 100%;
}
</style>
