<template>
    <el-tooltip :content="tooltipContent" :placement="tooltipPlacement" :disabled="disabled || !tooltipContent"
        v-if="disabled === false || disabledTip !== false">
        <el-dropdown split-button :type="type" :class="className" :disabled="disabled" :title="title" :size="size"
            @click="clickButton" @command="clickItem" v-if="dropdownItems.length">
            <i :class="icon" style="margin-right: 0.5rem;" v-if="icon"></i>
            <template v-if="!hiddenCaption">
                <slot v-if="$slots.default"></slot>
                <template v-else-if="menuItem">{{ menuItem.caption }}</template>
            </template>
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item v-for="dropdownItem of dropdownItems" :key="dropdownItem.path"
                        :icon="dropdownItem.icon" :disabled="dropdownItem.disabled" :title="dropdownItem.title"
                        :command="dropdownItem">
                        {{ dropdownItem.caption || (dropdownItem.menuItem ? dropdownItem.menuItem.caption : '') }}
                    </el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
        <el-button :type="type" :class="className" :disabled="disabled" :title="title" @click="clickButton" :size="size"
            :loading="loading" :plain="plain" :autofocus="autofocus" :round="round" :circle="circle" v-else>
            <tnxel-icon :value="icon" v-if="icon"/>
            <span v-if="!hiddenCaption && ($slots.default || menuItem)">
                <slot>{{ menuItem ? menuItem.caption : '' }}</slot>
            </span>
        </el-button>
    </el-tooltip>
</template>

<script>
import Icon from '../icon/Icon';

export default {
    name: 'TnxelButton',
    components: {
        'tnxel-icon': Icon,
    },
    props: {
        menu: Object,
        path: String,
        granted: { // 是否已授权可用，默认不指定，如果指定值，则不再根据menu和path判断是否授权可用
            type: Boolean,
            default: null,
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
        hiddenCaption: Boolean,
        dropdown: [Object, Array],
    },
    emits: ['click'],
    data() {
        let menuItem = null;
        if (this.menu && this.path) {
            menuItem = this.menu.getItemByPath(this.path);
        }
        return {
            menuItem: menuItem,
            disabled: null,
            dropdownItems: [],
        }
    },
    computed: {
        className() {
            return this.$attrs.class;
        },
        tooltipContent() {
            let content = this.tooltip;
            if (content === false) {
                return undefined;
            }
            if (typeof content === 'string' && content) {
                return content;
            }
            if (content === true && this.menuItem) {
                return this.menuItem.desc || this.menuItem.caption;
            }
            return undefined;
        },
        title() {
            return this.disabled ? this.disabledTip : this.$attrs.title;
        }
    },
    created() {
        this.init();
    },
    watch: {
        granted() {
            this.init();
        },
        dropdown() {
            this.buildDropdownItems();
        },
    },
    methods: {
        init() {
            if (this.granted !== null) {
                this.disabled = !this.granted;
            } else if (this.menu && this.path) {
                let vm = this;
                this.menu.loadGrantedItems(function() {
                    vm.disabled = !vm.menu.isGranted(vm.path);
                    vm.buildDropdownItems();
                });
            }
        },
        buildDropdownItems() {
            let dropdownItems = [];
            if (this.dropdown) {
                let dropdowns = Array.isArray(this.dropdown) ? this.dropdown : [this.dropdown];
                for (let dropdown of dropdowns) {
                    let dropdownItem = typeof dropdown === 'string' ? {
                        path: dropdown
                    } : (dropdown || {});

                    if (this.menu && dropdownItem.path) {
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
            if (!item.disabled && this.$router) {
                if (typeof this.click === 'function') {
                    this.click(item.path);
                } else if (this.click !== true && item.menuItem && item.menuItem.path) { // click属性为true时执行点击动作而不是跳转
                    let vm = this;
                    this.$router.push(item.path).catch(function() {
                        // 指定路径无法跳转，则触发点击事件
                        vm.$emit('click', item.path);
                    });
                } else { // 触发点击事件兜底
                    this.$emit('click', item.path);
                }
            }
        }
    }
}
</script>
