<template>
    <el-cascader v-model="model" class="ignore-feedback" :options="items" :props="options"
        :placeholder="placeholder" :show-all-levels="showAllLevels" :clearable="empty" :disabled="disabled"
        :filterable="filterable" :filter-method="filter"/>
</template>

<script>
export default {
    name: 'TnxelFetchCascader',
    props: {
        value: String,
        url: {
            type: String,
            required: true,
        },
        params: Object,
        valueName: {
            type: String,
            default: () => 'id',
        },
        textName: {
            type: String,
            default: () => 'name',
        },
        indexName: {
            type: String,
            default: 'index',
        },
        childrenName: {
            type: String,
            default: () => 'children',
        },
        leafName: {
            type: String,
            default: () => 'leaf',
        },
        showAllLevels: {
            type: Boolean,
            default: true,
        },
        empty: Boolean,
        placeholder: {
            type: String,
            default: '请选择',
        },
        disabled: Boolean,
        change: Function, // 选中值变化后的事件处理函数，由于比element的change事件传递更多参数，所以以prop的形式指定，以尽量节省性能
        transferItems: {
            type: Function,
            default: function(items) {
                return items;
            }
        },
        filterable: Boolean,
    },
    data() {
        return {
            options: {
                expandTrigger: 'hover',
                emitPath: false,
                value: this.valueName,
                label: this.textName,
                children: this.childrenName,
                leaf: this.leafName,
            },
            items: null,
            model: null,
        };
    },
    watch: {
        model(value) {
            this.$emit('input', value);
            this.triggerChange(value);
        },
        value(value) {
            this.model = this.getModel();
        }
    },
    created() {
        this.load();
    },
    methods: {
        triggerChange(value) {
            if (this.change) {
                let item = this.getItem(this.items, value);
                this.change(item);
            }
        },
        getItem(items, value) {
            if (items && value !== undefined) {
                for (let item of items) {
                    if (item[this.valueName] === value) {
                        return item;
                    }
                    let children = item[this.childrenName];
                    let child = this.getItem(children, value);
                    if (child) {
                        return child;
                    }
                }
            }
            return undefined;
        },
        getModel() {
            if (this.items && this.items.length) {
                let item = this.getItem(this.items, this.value);
                if (item) {
                    return this.value;
                } else { // 如果当前值找不到匹配的选项，则需要考虑是设置为空还是默认选项
                    if (!this.empty) { // 如果不能为空，则默认选中第一个选项
                        let firstItem = this.items[0];
                        while (firstItem[this.childrenName] && firstItem[this.childrenName].length) {
                            firstItem = firstItem[this.childrenName][0];
                        }
                        return firstItem ? firstItem[this.valueName] : null;
                    }
                }
            }
            return null;
        },
        load() {
            let vm = this;
            window.tnx.app.rpc.get(this.url, this.params, function(result) {
                vm.items = vm.transferItems(result);
                vm.model = vm.getModel();
            });
        },
        filter(node, keyword) {
            let data = node.data;
            return !keyword || window.tnx.util.string.matchesForEach(data[this.valueName], keyword)
                || window.tnx.util.string.matchesForEach(data[this.textName], keyword)
                || window.tnx.util.string.matchesForEach(data[this.indexName], keyword)
        }
    }
}
</script>
