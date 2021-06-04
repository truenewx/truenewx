<template>
    <el-transfer v-model="selected" :data="selectable" :titles="titles"
        :filterable="filterable" :filter-method="filter" :filter-placeholder="placeholder"/>
</template>

<script>
export default {
    name: 'TnxelTransfer',
    props: {
        value: {
            type: Array,
            required: true,
        },
        url: {
            type: String,
            required: true,
        },
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
        }
    },
    data() {
        return {
            selectable: [],
            selected: this.value,
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
        window.tnx.app.rpc.get(this.url, function(list) {
            vm.selectable = [];
            list.forEach(item => {
                vm.selectable.push({
                    key: item[vm.keyName],
                    label: item[vm.labelName],
                    index: item[vm.indexName],
                });
            });
        });
    },
    watch: {
        value(value) {
            this.selected = value;
        },
        selected(value) {
            this.$emit('input', value);
        }
    },
    methods: {
        filter(keyword, item) {
            return item.label.contains(keyword) || (item.index && item.index.contains(keyword));
        }
    }
}
</script>
