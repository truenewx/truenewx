<template>
    <el-checkbox-group v-model="model" :theme="theme" :size="size" :disabled="disabled" v-if="selector === 'checkbox'">
        <el-checkbox v-for="item in items" :key="item[valueName]" :label="item[valueName]"
            :data-value="item[valueName]">
            <i :class="item[iconName]" v-if="item[iconName]"></i>
            <span>{{ item[textName] }}</span>
        </el-checkbox>
        <template v-if="items.length === 0">
            <slot name="empty" v-if="$slots.empty"></slot>
            <span class="text-muted" :class="emptyClass" v-else-if="emptyText">{{ emptyText }}</span>
        </template>
    </el-checkbox-group>
    <div class="tnxel-tag-group d-flex flex-wrap" v-else-if="selector === 'tag' || selector === 'tags'">
        <template v-if="items">
            <el-button link :size="size" :class="emptyClass" v-if="emptyText" @click="clear">
                {{ emptyText }}
            </el-button>
            <el-tag v-for="item in items" :key="item[valueName]" :type="theme"
                :effect="isSelected(item[valueName]) ? 'dark' : 'plain'" :data-value="item[valueName]"
                @click="select(item[valueName])">
                <i :class="item[iconName]" v-if="item[iconName]"></i>
                <span>{{ item[textName] }}</span>
            </el-tag>
            <template v-if="items.length === 0">
                <slot name="empty"></slot>
            </template>
        </template>
        <i class="el-icon-loading" v-else/>
    </div>
    <div class="tnxel-text-button-group d-flex flex-wrap" v-else-if="selector === 'text' || selector === 'texts'">
        <template v-if="items">
            <el-button link :size="size" :class="emptyClass" v-if="emptyText" @click="clear">
                {{ emptyText }}
            </el-button>
            <el-button v-for="item in items" :key="item[valueName]"
                :link="!isSelected(item[valueName])" :plain="isSelected(item[valueName])"
                :size="size" :data-value="item[valueName]" @click="select(item[valueName], $event)">
                <i :class="item[iconName]" v-if="item[iconName]"></i>
                <span>{{ item[textName] }}</span>
            </el-button>
            <template v-if="items.length === 0">
                <slot name="empty"></slot>
            </template>
        </template>
        <i class="el-icon-loading" v-else/>
    </div>
    <el-radio-group v-model="model" class="ignore-feedback" :theme="theme" :size="size" :disabled="disabled"
        v-else-if="selector === 'radio'">
        <el-radio :label="emptyValue" :class="emptyClass" v-if="empty">{{ emptyText }}</el-radio>
        <el-radio v-for="item in items" :key="item[valueName]" :label="item[valueName]" :data-value="item[valueName]">
            <i :class="item[iconName]" v-if="item[iconName]"></i>
            <span>{{ item[textName] }}</span>
        </el-radio>
    </el-radio-group>
    <el-radio-group v-model="model" class="ignore-feedback" :theme="theme" :size="size" :disabled="disabled"
        v-else-if="selector === 'radio-button'">
        <el-radio-button :class="emptyClass" :label="emptyValue" v-if="empty">{{ emptyText }}</el-radio-button>
        <el-radio-button v-for="item in items" :key="item[valueName]" :label="item[valueName]"
            :data-value="item[valueName]">
            <i :class="item[iconName]" v-if="item[iconName]"></i>
            <span>{{ item[textName] }}</span>
        </el-radio-button>
    </el-radio-group>
    <el-dropdown trigger="click" :size="size" @command="onDropdownCommand" v-else-if="selector === 'dropdown'">
        <el-button style="width: 100%" :type="theme">
            <div class="d-flex justify-content-between">
                <span>{{ currentText }}</span>
                <tnxel-icon value="ArrowDown"/>
            </div>
        </el-button>
        <template #dropdown v-if="items && items.length">
            <el-dropdown-menu>
                <el-dropdown-item v-for="item in items" :key="item[valueName]" :command="item[valueName]"
                    :data-value="item[valueName]">
                    <i :class="item[iconName]" v-if="item[iconName]"></i>
                    <span>{{ item[textName] }}</span>
                </el-dropdown-item>
            </el-dropdown-menu>
        </template>
    </el-dropdown>
    <el-dropdown :type="theme" :size="size" trigger="click" split-button @command="onDropdownCommand"
        v-else-if="selector === 'split-dropdown'">
        <span>{{ currentText }}</span>
        <template #dropdown v-if="items && items.length">
            <el-dropdown-menu>
                <el-dropdown-item v-for="item in items" :key="item[valueName]" :command="item[valueName]"
                    :data-value="item[valueName]">
                    <i :class="item[iconName]" v-if="item[iconName]"></i>
                    <span>{{ item[textName] }}</span>
                </el-dropdown-item>
            </el-dropdown-menu>
        </template>
    </el-dropdown>
    <el-select v-model="model" class="ignore-feedback" :placeholder="placeholder" :theme="theme" :size="size"
        :disabled="disabled" :filterable="filterable" :filter-method="filter" v-else>
        <el-option class="text-muted" :value="emptyValue" :label="emptyText" :class="emptyClass" v-if="empty"/>
        <template v-for="item in items">
            <el-option :key="item[valueName]" :value="item[valueName]" :label="item[textName]"
                :data-value="item[valueName]" v-if="!hiddenValues.contains(item[valueName])">
                <slot name="option" :item="item"></slot>
            </el-option>
        </template>
    </el-select>
</template>

<script>
import Icon from '../icon/Icon';

export const isMultiSelector = function(selector) {
    return selector === 'checkbox' || selector === 'tags' || selector === 'texts';
}
export default {
    name: 'TnxelSelect',
    components: {
        'tnxel-icon': Icon,
    },
    props: {
        id: [Number, String],
        modelValue: {
            type: [String, Number, Boolean, Array],
            default: null,
        },
        selector: String,
        items: Array,
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
        iconName: {
            type: String,
            default: 'icon',
        },
        defaultValue: {
            type: [String, Number, Boolean, Array],
            default: null,
        },
        empty: {
            type: [Boolean, String],
            default: false,
        },
        emptyValue: {
            type: [String, Number, Boolean, Array],
            default: null,
        },
        emptyClass: String,
        placeholder: String,
        disabled: Boolean,
        tagClick: Function, // 点击一个标签选项时调用，如果返回false，则选项不会被选中
        change: Function, // 选中值变化后的事件处理函数，由于比element的change事件传递更多参数，所以以prop的形式指定，以尽量节省性能
        filterable: Boolean,
        theme: String,
        size: String,
    },
    emits: ['update:modelValue'],
    data() {
        let model = this.getModel(this.items);
        if (model !== this.modelValue) {
            this.$emit('update:modelValue', model);
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
        },
    },
    watch: {
        model(newValue, oldValue) {
            this.$emit('update:modelValue', newValue);
            // 新旧值不同时为空才触发变更事件
            const util = window.tnx.util;
            if (util.object.isNotEmpty(newValue) || util.object.isNotEmpty(oldValue)) {
                let vm = this;
                // 确保变更事件在值变更应用后再触发
                this.$nextTick(function() {
                    vm.triggerChange(newValue);
                });
            }
        },
        modelValue() {
            this.model = this.getModel(this.items);
        },
        items(items) {
            this.model = this.getModel(items);
        },
    },
    methods: {
        isMulti() {
            return isMultiSelector(this.selector);
        },
        triggerChange(value) {
            if (this.change) {
                let item = undefined;
                if (this.isMulti()) {
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
            if (this.empty && value === this.emptyValue) {
                let item = {};
                item[this.valueName] = this.emptyValue;
                item[this.textName] = this.emptyText;
                return item;
            }
            if (value !== undefined && value !== null && this.items) {
                for (let item of this.items) {
                    if ((item[this.valueName] + '') === (value + '')) {
                        return item;
                    }
                }
            }
            return undefined;
        },
        getText(value) {
            let item = this.getItem(value);
            return item ? item[this.textName] : undefined;
        },
        getModel(items) {
            const util = window.tnx.util;
            let model = this.modelValue;
            if (util.object.isNull(model)) {
                model = this.defaultValue;
            }
            if (this.isMulti()) { // 多选时需确保值为数组
                if (util.object.isNull(model)) {
                    return [];
                }
                if (!Array.isArray(model)) {
                    model = [model];
                }
                return model;
            }
            if (util.object.isNull(model)) {
                return null;
            }
            if (items?.length) {
                let item = this.getItem(model);
                if (item) {
                    return item[this.valueName];
                } else { // 如果当前值找不到匹配的选项，则需要考虑是设置为空还是默认选项
                    if (!this.empty) { // 如果不能为空，则默认选中第一个选项
                        let firstItem = items[0];
                        if (firstItem[this.valueName]) {
                            return firstItem[this.valueName];
                        }
                    }
                }
            }
            return model;
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
        },
        isSelected(value) {
            if (Array.isArray(this.model)) {
                return this.model.contains(value);
            } else {
                return this.model === value;
            }
        },
        select(value, event) {
            if (this.tagClick) {
                let item = this.getItem(value);
                if (item) {
                    if (this.tagClick(item) === false) {
                        return;
                    }
                }
            }
            if (this.isMulti()) {
                let index = this.model.indexOf(value);
                if (index >= 0) {
                    this.model = this.model.filter(function(e, i) {
                        return i !== index;
                    });
                } else {
                    this.model = this.model.concat([value]);
                }
            } else {
                this.model = value;
            }
            if (event) {
                event.currentTarget.blur();
            }
        },
        clear() {
            if (this.isMulti()) {
                this.model = [];
            } else {
                this.model = null;
            }
        },
    }
}
</script>

<style>
.tnxel-tag-group .el-tag {
    margin-top: 5px;
    margin-bottom: 5px;
    cursor: pointer;
}

.tnxel-tag-group .el-tag:not(:last-child) {
    margin-right: 10px;
}

.tnxel-tag-group .el-button,
.tnxel-text-button-group .el-button {
    padding: 0.5rem 0.75rem;
    margin: 2px 0.5rem 2px 0;
}

.tnxel-tag-group .el-button__text--expand,
.tnxel-text-button-group .el-button__text--expand {
    letter-spacing: unset;
    margin-right: unset;
}

.tnxel-tag-group .el-button.is-link,
.tnxel-text-button-group .el-button.is-link {
    color: unset;
}

.tnxel-tag-group .el-button.is-link:hover,
.tnxel-text-button-group .el-button.is-link:hover {
    color: var(--el-color-primary);
    border-color: transparent;
}

.tnxel-text-button-group .el-button.is-plain {
    border-color: var(--el-color-primary);
    color: var(--el-color-primary);
}

.tnxel-text-button-group .el-button + .el-button {
    margin-left: 0;
}
</style>
