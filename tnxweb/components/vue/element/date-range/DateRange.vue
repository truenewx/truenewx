<template>
    <el-date-picker type="daterange" v-model="model" :editable="false" value-format="yyyy-MM-dd" :clearable="empty"
        :range-separator="separator" :start-placeholder="beginPlaceholder" :end-placeholder="endPlaceholder"
        :disabled="disabled"/>
</template>

<script>
export default {
    name: 'TnxelDateRange',
    props: {
        value: Object,
        separator: {
            type: String,
            default: '至',
        },
        beginPlaceholder: {
            type: String,
            default: '开始日期',
        },
        endPlaceholder: {
            type: String,
            default: '结束日期',
        },
        disabled: Boolean,
        empty: {
            type: Boolean,
            default: false,
        }
    },
    data() {
        return {
            model: this.getModel(),
        };
    },
    watch: {
        model(model) {
            let value = {};
            if (Array.isArray(model)) {
                if (model.length > 0) {
                    value.begin = model[0];
                }
                if (model.length > 1) {
                    value.end = model[1];
                }
            }
            this.$emit('input', value);
        },
        value(value) {
            let model = this.getModel();
            if (model) {
                if (this.model === null) {
                    this.model = [];
                }
                this.model[0] = model[0];
                this.model[1] = model[1];
            } else {
                this.model = null;
            }
        }
    },
    methods: {
        getModel() {
            let model = null;
            if (this.value && this.value.begin && this.value.end) {
                model = [];
                model.push(this.value.begin instanceof Date ? this.value.begin.formatDate() : this.value.begin);
                model.push(this.value.end instanceof Date ? this.value.end.formatDate() : this.value.end);
            }
            return model;
        }
    }
}
</script>
