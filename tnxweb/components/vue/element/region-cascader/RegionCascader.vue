<template>
    <el-cascader v-model="model" class="ignore-feedback" :options="region.subs" :props="options"
        :placeholder="placeholder" :disabled="disabled" :clearable="empty"/>
</template>

<script>
export default {
    name: 'TnxelRegionCascader',
    props: {
        value: String,
        scope: {
            type: String,
            default: () => 'CN',
        },
        maxLevel: {
            type: [Number, String],
            default: 3,
        },
        minLevel: {
            type: [Number, String],
            default: 3,
        },
        empty: {
            type: Boolean,
            default: false,
        },
        placeholder: String,
        disabled: Boolean,
        change: Function, // 选中值变化后的事件处理函数，由于比element的change事件传递更多参数，所以以prop的形式指定，以尽量节省性能
    },
    data() {
        // 最小级别小于最大级别，则取消父子节点选中关联，允许选择中间级别的节点
        let checkStrictly = parseInt(this.minLevel) < parseInt(this.maxLevel);
        return {
            options: {
                expandTrigger: 'hover',
                emitPath: false,
                value: 'code',
                label: 'caption',
                children: 'subs',
                leaf: 'includingSub',
                checkStrictly: checkStrictly,
            },
            model: null,
            region: {},
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
        let vm = this;
        window.tnx.app.rpc.loadRegion(this.scope, parseInt(this.maxLevel), function(region) {
            vm.region = region;
            vm.model = vm.getModel();
        });
    },
    methods: {
        triggerChange(value) {
            if (this.change) {
                let item = this.getItem(this.region.subs, value);
                this.change(item);
            }
        },
        getItem(items, value) {
            if (items && value !== undefined) {
                for (let item of items) {
                    if (item.code === value) {
                        return item;
                    }
                    let sub = this.getItem(item.subs, value);
                    if (sub) {
                        return sub;
                    }
                }
            }
            return undefined;
        },
        getModel() {
            if (this.region) {
                let items = this.region.subs;
                if (items && items.length) {
                    let item = this.getItem(items, this.value);
                    if (item) {
                        return this.value;
                    } else { // 如果当前值找不到匹配的选项，则需要考虑是设置为空还是默认选项
                        if (!this.empty) { // 如果不能为空，则默认选中第一个叶子节点选项
                            let firstItem = items[0];
                            while (firstItem.subs && firstItem.subs.length) {
                                firstItem = firstItem.subs[0];
                            }
                            return firstItem ? firstItem[this.valueName] : null;
                        }
                    }
                }
            }
            return null;
        }
    }
}
</script>
