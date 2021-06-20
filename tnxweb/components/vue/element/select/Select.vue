<template>
    <el-checkbox-group v-model="model" :theme="theme" :size="size" :disabled="disabled" v-if="selector === 'checkbox'">
        <el-checkbox v-for="item in items" :key="item[valueName]" :label="item[valueName]">
            {{ item[textName] }}
        </el-checkbox>
    </el-checkbox-group>
    <el-radio-group v-model="model" class="ignore-feedback" :theme="theme" :size="size" :disabled="disabled"
        v-else-if="selector === 'radio'">
        <el-radio :label="emptyValue" v-if="empty">{{ emptyText }}</el-radio>
        <el-radio v-for="item in items" :key="item[valueName]" :label="item[valueName]">
            {{ item[textName] }}
        </el-radio>
    </el-radio-group>
    <el-radio-group v-model="model" class="ignore-feedback" :theme="theme" :size="size" :disabled="disabled"
        v-else-if="selector === 'radio-button'">
        <el-radio-button :label="emptyValue" v-if="empty">{{ emptyText }}</el-radio-button>
        <el-radio-button v-for="item in items" :key="item[valueName]" :label="item[valueName]">
            {{ item[textName] }}
        </el-radio-button>
    </el-radio-group>
    <el-dropdown trigger="click" :size="size" @command="onDropdownCommand" v-else-if="selector === 'dropdown'">
        <el-button :type="theme">{{ currentText }}
            <i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown" v-if="items && items.length">
            <el-dropdown-item v-for="item in items" :key="item[valueName]" :command="item[valueName]">
                {{ item[textName] }}
            </el-dropdown-item>
        </el-dropdown-menu>
    </el-dropdown>
    <el-dropdown :type="theme" :size="size" trigger="click" split-button @command="onDropdownCommand"
        v-else-if="selector === 'split-dropdown'">
        <span>{{ currentText }}</span>
        <el-dropdown-menu slot="dropdown" v-if="items && items.length">
            <el-dropdown-item v-for="item in items" :key="item[valueName]" :command="item[valueName]">
                {{ item[textName] }}
            </el-dropdown-item>
        </el-dropdown-menu>
    </el-dropdown>
    <el-select v-model="model" class="ignore-feedback" :placeholder="placeholder" :theme="theme" :size="size"
        :disabled="disabled" :filterable="filterable" :filter-method="filter" v-else>
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
        id: [Number, String],
        value: [String, Number, Boolean],
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
        theme: String,
        size: String,
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
        },
        currentText() {
            let item = this.getItem(this.model);
            return item ? item[this.textName] : undefined;
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
                this.change(item, this.id);
            }
        },
        getItem(value) {
            if (value !== undefined && value !== null && this.items) {
                for (let item of this.items) {
                    if ((item[this.valueName] + '') === (value + '')) {
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
                    return item[this.valueName];
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
        },
        onDropdownCommand(value) {
            this.model = value;
        }
    }
}
</script>
