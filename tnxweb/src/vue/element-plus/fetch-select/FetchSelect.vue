<template>
    <el-select v-model="model" :loading="loading" :filterable="filterable" remote :remote-method="load"
        :placeholder="finalPlaceholder" :disabled="disabled" :title="title" default-first-option>
        <el-option class="text-secondary" :value="null" :label="emptyText" v-if="empty"/>
        <el-option v-for="item in items" :key="item[valueName]" :value="item[valueName]" :label="label(item)"/>
        <el-option label="还有更多结果..." disabled v-if="more"/>
    </el-select>
</template>

<script>
export default {
    name: 'TnxelFetchSelect',
    props: {
        modelValue: [String, Number],
        url: String,
        params: { // 构建远程检索请求参数集的函数
            type: Function,
            default: function(keyword) {
                return keyword ? {keyword} : undefined;
            }
        },
        resultName: {  // 从返回结果中取结果清单的字段名称，常用于从分页查询结果中获取记录清单，仅当返回结果不是数组而是对象时有效
            type: String,
            default: () => 'records',
        },
        valueName: {
            type: String,
            default: () => 'id',
        },
        textName: {
            type: String,
            default: () => 'name',
        },
        descName: String,
        empty: {
            type: [Boolean, String],
            default: false,
        },
        filterable: Boolean,
        placeholder: [String, Array],
        disabled: Boolean,
        change: Function, // 选中值变化后的事件处理函数，由于比element的change事件传递更多参数，所以以prop的形式指定，以尽量节省性能
    },
    emits: ['update:modelValue', 'items'],
    data() {
        return {
            items: null,
            model: null, // 初始情况下，items为空，model必然为空
            loading: false,
            more: false,
        };
    },
    computed: {
        finalPlaceholder() {
            let defaultPlaceholder;
            let noItemsPlaceholder;
            if (Array.isArray(this.placeholder)) {
                defaultPlaceholder = this.placeholder[0];
                noItemsPlaceholder = this.placeholder[1];
            } else {
                defaultPlaceholder = this.placeholder;
                noItemsPlaceholder = '没有可选项';
            }
            if (this.items && this.items.length === 0) {
                return noItemsPlaceholder;
            }
            if (defaultPlaceholder) {
                return defaultPlaceholder;
            }
            return this.filterable ? '输入关键字进行检索' : '请选择';
        },
        title() {
            return this.model ? undefined : this.finalPlaceholder;
        },
        emptyText() {
            return typeof this.empty === 'string' ? this.empty : '';
        },
    },
    watch: {
        model(value) {
            this.$emit('update:modelValue', value);
            this.triggerChange(value);
        },
        modelValue() {
            this.model = this.getModel();
        },
        url() {
            this.load();
        },
        params() {
            this.load();
        }
    },
    created() {
        this.load();
    },
    methods: {
        label(item) {
            let label = item[this.textName];
            if (this.descName && item[this.descName]) {
                label += ' (' + item[this.descName] + ')';
            }
            return label;
        },
        triggerChange(value) {
            if (this.change) {
                let item = this.getItem(value);
                this.change(item);
            }
        },
        getItem(value) {
            if (value !== undefined && value !== null && this.items) {
                for (let item of this.items) {
                    if ((item[this.valueName] + '') === (value + '')) { // 确保一致的字符串值比较
                        return item;
                    }
                }
            }
            return undefined;
        },
        load(keyword) {
            if (this.url && this.params) { // 当url或参数函数被设置为null时，不进行取数操作，用于初始条件不满足的情况
                this.loading = true;
                let params = this.params(keyword);
                let vm = this;
                window.tnx.app.rpc.get(this.url, params, function(result) {
                    vm.loading = false;
                    if (Array.isArray(result)) {
                        vm.items = result;
                    } else if (typeof result === 'object') {
                        vm.items = result[vm.resultName];
                        if (result.paged) {
                            vm.more = result.paged.morePage;
                        }
                    }
                    // 确保选项的值均为字符串，以保证比较时的一致性
                    if (Array.isArray(vm.items)) {
                        vm.items.forEach(item => {
                            item[vm.valueName] += '';
                        });
                    }
                    vm.$emit('items', params, vm.items, vm.more);
                    vm.model = vm.getModel();
                });
            }
        },
        getModel() {
            if (this.items && this.items.length) {
                let item = this.getItem(this.modelValue);
                if (item) {
                    return item[this.valueName];
                } else { // 如果当前值找不到匹配的选项，则需要考虑是设置为空还是默认选项
                    if (!this.filterable && !this.empty) { // 如果不可检索且不能为空，则默认选中第一个选项
                        let firstItem = this.items[0];
                        return firstItem ? firstItem[this.valueName] : null;
                    } else { // 否则设置为空
                        return null;
                    }
                }
            } else {
                return null;
            }
        },
    }
}
</script>
