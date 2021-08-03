<template>
    <el-tooltip :content="tooltipContent" :placement="tooltipPlacement" :disabled="disabled || !tooltipContent"
        v-if="disabled === false || disabledTip !== false">
        <el-dropdown split-button :type="type" :disabled="disabled" :title="title" :size="size" @click="clickButton"
            @command="clickItem" v-if="dropdownItems.length">
            <i :class="icon" style="margin-right: 0.5rem;" v-if="icon"></i>
            <slot v-if="$slots.default"></slot>
            <template v-else-if="menuItem">{{ menuItem.caption }}</template>
            <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-for="dropdownItem of dropdownItems" :key="dropdownItem.path"
                    :icon="dropdownItem.icon" :disabled="dropdownItem.disabled" :title="dropdownItem.title"
                    :command="dropdownItem">
                    {{ dropdownItem.caption || (dropdownItem.menuItem ? dropdownItem.menuItem.caption : '') }}
                </el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>
        <el-button :type="type" :icon="icon" :disabled="disabled" :title="title" @click="clickButton" :size="size"
            :loading="loading" :plain="plain" :autofocus="autofocus" :round="round" :circle="circle" v-else>
            <slot v-if="$slots.default"></slot>
            <template v-else-if="menuItem">{{ menuItem.caption }}</template>
        </el-button>
    </el-tooltip>
</template>

<script>
export default {
    name: 'TnxelButton',
    props: {
        menu: {
            type: Object,
            required: true,
        },
        path: {
            type: String,
            required: true,
        },
        click: {
            type: [Function, Boolean],
            default: false,
        },
        type: String,
        icon: String,
        size: String,
        loading: Boolean,
        plain: Boolean,
        autofocus: Boolean,
        round: Boolean,
        circle: Boolean,
        tooltip: {
            type: [String, Boolean],
            default: '',
        },
        tooltipPlacement: {
            type: String,
            default: 'top',
        },
        disabledTip: {
            type: [String, Boolean],
            default: '没有操作权限',
        },
        dropdown: [Object, Array],
    },
    data() {
        return {
            menuItem: this.menu.getItemByPath(this.path),
            disabled: null,
            dropdownItems: [],
        }
    },
    computed: {
        tooltipContent() {
            let content = this.tooltip;
            if (content === false) {
                return undefined;
            }
            if (!content && this.menuItem) {
                content = this.menuItem.desc;
            }
            return content;
        },
        title() {
            return this.disabled ? this.disabledTip : undefined;
        }
    },
    created() {
        let vm = this;
        this.menu.loadGrantedItems(function() {
            vm.disabled = !vm.menu.isGranted(vm.path);
            vm.buildDropdownItems();
        });
    },
    watch: {
        dropdown() {
            this.buildDropdownItems();
        }
    },
    methods: {
        buildDropdownItems() {
            let dropdownItems = [];
            if (this.dropdown) {
                let dropdowns = Array.isArray(this.dropdown) ? this.dropdown : [this.dropdown];
                for (let dropdown of dropdowns) {
                    let dropdownItem = typeof dropdown === 'string' ? {
                        path: dropdown
                    } : (dropdown || {});

                    if (dropdownItem.path) {
                        dropdownItem.disabled = !this.menu.isGranted(dropdownItem.path);
                        if (dropdownItem.disabled && typeof this.disabledTip === 'string') {
                            dropdownItem.title = this.disabledTip;
                        }
                        dropdownItem.menuItem = this.menu.getItemByPath(dropdownItem.path);
                    }

                    dropdownItems.push(dropdownItem);
                }
            }
            this.dropdownItems = dropdownItems;
        },
        clickButton() {
            this.clickItem(this);
        },
        clickItem(item) {
            if (!item.disabled && item.menuItem && this.$router) {
                if (typeof this.click === 'function') {
                    this.click(item.path);
                } else if (this.click) { // 简单指定click为true，则触发点击事件
                    this.$emit('click', item.path);
                } else if (item.menuItem.path) {
                    let vm = this;
                    this.$router.push(item.path).catch(function() {
                        // 指定路径无法跳转，则触发点击事件
                        vm.$emit('click', item.path);
                    });
                } else { // 匹配菜单项未配置路径，则触发点击事件
                    this.$emit('click', item.path);
                }
            }
        }
    }
}
</script>
