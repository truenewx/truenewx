<template>
    <el-transfer v-model="selected" :data="selectable" :titles="titles"
        :filterable="filterable" :filter-method="filter" :filter-placeholder="placeholder"/>
</template>

<script>
export default {
    name: 'TnxelTransfer',
    props: {
        modelValue: {
            type: Array,
            default() {
                return [];
            }
        },
        url: {
            type: String,
            required: true,
        },
        params: [Object, Array],
        title: {
            type: [String, Array]
        },
        filterable: {
            type: Boolean,
            default: true,
        },
        placeholder: {
            type: String,
            default: () => '输入关键字进行筛选',
        },
        keyName: {
            type: String,
            default: () => 'id',
        },
        labelName: {
            type: String,
            default: () => 'caption',
        },
        indexName: {
            type: String,
            default: () => 'index',
        },
        formatter: Function,
    },
    emits: ['update:modelValue'],
    data() {
        return {
            selectable: [],
            selected: this.modelValue,
        }
    },
    computed: {
        titles() {
            if (typeof this.title === 'string') {
                return ['可选' + this.title, '已选' + this.title];
            }
            return this.title;
        }
    },
    created() {
        let vm = this;
        let params;
        if (typeof this.params === 'function') {
            params = this.params();
        } else {
            params = this.params;
        }
        window.tnx.app.rpc.get(this.url, params, function(list) {
            vm.selectable = [];
            list.forEach(item => {
                if (vm.formatter) {
                    vm.formatter(item);
                }
                vm.selectable.push({
                    key: item[vm.keyName],
                    label: item[vm.labelName],
                    index: item[vm.indexName],
                });
            });
        });
    },
    watch: {
        modelValue(value) {
            this.selected = value;
        },
        selected(value) {
            this.$emit('update:modelValue', value);
        }
    },
    methods: {
        filter(keyword, item) {
            return (item.label && item.label.contains(keyword)) || (item.index && item.index.contains(keyword));
        }
    }
}
</script>
