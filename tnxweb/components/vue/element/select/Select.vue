<template>
    <el-checkbox-group v-model="model" :disabled="disabled" v-if="selector === 'checkbox'">
        <el-checkbox v-for="item in items" :key="item[valueName]" :label="item[valueName]">
            {{ item[textName] }}
        </el-checkbox>
    </el-checkbox-group>
    <el-radio-group v-model="model" class="ignore-feedback" :disabled="disabled" v-else-if="selector === 'radio'">
        <el-radio :label="emptyValue" v-if="empty">{{ emptyText }}</el-radio>
        <el-radio v-for="item in items" :key="item[valueName]" :label="item[valueName]">
            {{ item[textName] }}
        </el-radio>
    </el-radio-group>
    <el-radio-group v-model="model" class="ignore-feedback" :disabled="disabled"
        v-else-if="selector === 'radio-button'">
        <el-radio-button :label="emptyValue" v-if="empty">{{ emptyText }}</el-radio-button>
        <el-radio-button v-for="item in items" :key="item[valueName]" :label="item[valueName]">
            {{ item[textName] }}
        </el-radio-button>
    </el-radio-group>
    <el-select v-model="model" class="ignore-feedback" :placeholder="placeholder" :disabled="disabled"
        :filterable="filterable" :filter-method="filter" v-else>
        <el-option class="text-muted" :value="emptyValue" :label="emptyText" v-if="empty"/>
        <template v-for="item in items">
            <el-option :key="item[valueName]" :value="item[valueName]" :label="item[textName]"
                v-if="!hiddenValues.contains(item[valueName])"/>
        </template>
    </el-select>
</template>

<script>
export default {
    name: 'TnxelSelect',
    props: {
        value: String,
        selector: String,
        items: {
            type: Array,
            required: true,
        },
        valueName: {
            type: String,
            default: 'value',
        },
        textName: {
            type: String,
            default: 'text',
        },
        indexName: {
            type: String,
            default: 'index',
        },
        defaultValue: String,
        empty: {
            type: [Boolean, String],
            default: false,
        },
        emptyValue: {
            type: [String, Boolean, Number],
            default: () => null,
        },
        placeholder: String,
        disabled: Boolean,
        change: Function, // 选中值变化后的事件处理函数，由于比element的change事件传递更多参数，所以以prop的形式指定，以尽量节省性能
        filterable: Boolean,
    },
    data() {
        let model = this.getModel(this.items);
        if (model !== this.value) {
            this.$emit('input', model);
        }
        return {
            model: model,
            hiddenValues: [],
        };
    },
    computed: {
        emptyText() {
            return typeof this.empty === 'string' ? this.empty : '';
        }
    },
    watch: {
        model(value) {
            this.$emit('input', value);
            this.triggerChange(value);
        },
        value(value) {
            this.model = this.getModel(this.items);
        },
        items(items) {
            this.model = this.getModel(items);
        },
    },
    methods: {
        triggerChange(value) {
            if (this.change) {
                let item = undefined;
                if (this.selector === 'checkbox') {
                    item = [];
                    if (Array.isArray(value)) {
                        for (let v of value) {
                            item.push(this.getItem(v));
                        }
                    }
                } else {
                    item = this.getItem(value);
                }
                this.change(item);
            }
        },
        getItem(value) {
            if (value !== undefined && value !== null && this.items) {
                for (let item of this.items) {
                    if (item[this.valueName] === value) {
                        return item;
                    }
                }
            }
            return undefined;
        },
        getModel(items) {
            let model = this.value || this.defaultValue;
            if (this.selector === 'checkbox') { // 多选时需确保值为数组
                if (model) {
                    if (!Array.isArray(model)) {
                        model = [model];
                    }
                } else {
                    model = [];
                }
                return model;
            } else if (items && items.length) {
                let item = this.getItem(this.value);
                if (item) {
                    return this.value;
                } else { // 如果当前值找不到匹配的选项，则需要考虑是设置为空还是默认选项
                    if (!this.empty) { // 如果不能为空，则默认选中第一个选项
                        let firstItem = items[0];
                        return firstItem ? firstItem[this.valueName] : null;
                    } else { // 否则设置为空
                        return null;
                    }
                }
            }
            return null;
        },
        filter(keyword) {
            for (let item of this.items) {
                let itemValue = item[this.valueName];
                let hiddenIndex = this.hiddenValues.indexOf(itemValue);
                if (this.matchesItem(item, keyword)) {
                    if (hiddenIndex >= 0) { // 匹配且原本已隐藏的，则取消隐藏
                        this.hiddenValues.splice(hiddenIndex, 1);
                    }
                } else {
                    if (hiddenIndex < 0) { // 不匹配且原本未隐藏的，则进行隐藏
                        this.hiddenValues.push(itemValue);
                    }
                }
            }
        },
        matchesItem(item, keyword) {
            return !keyword || window.tnx.util.string.matchesForEach(item[this.valueName], keyword)
                || window.tnx.util.string.matchesForEach(item[this.textName], keyword)
                || window.tnx.util.string.matchesForEach(item[this.indexName], keyword)
        }
    }
}
</script>
