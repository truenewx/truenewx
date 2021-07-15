<template>
    <el-tooltip :content="tooltip" :placement="tooltipPlacement" v-if="tooltip && !disabled">
        <el-button :type="type" :icon="icon" @click="click" :size="size"
            :loading="loading" :plain="plain" :autofocus="autofocus" :round="round" :circle="circle">
            <slot v-if="$slots.default"></slot>
            <template v-else-if="item">{{ item.caption }}</template>
        </el-button>
    </el-tooltip>
    <el-button :type="type" :icon="icon" @click="click" :disabled="disabled" :title="title" :size="size"
        :loading="loading" :plain="plain" :autofocus="autofocus" :round="round" :circle="circle" v-else>
        <slot v-if="$slots.default"></slot>
        <template v-else-if="item">{{ item.caption }}</template>
    </el-button>
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
        type: String,
        icon: String,
        size: String,
        loading: Boolean,
        plain: Boolean,
        autofocus: Boolean,
        round: Boolean,
        circle: Boolean,
        tooltip: String,
        tooltipPlacement: {
            type: String,
            default: 'top',
        },
        disabledTip: {
            type: String,
            default: '没有操作权限',
        }
    },
    data() {
        return {
            item: this.menu.getItemByPath(this.path),
            disabled: false,
        }
    },
    computed: {
        title() {
            return this.disabled ? this.disabledTip : undefined;
        }
    },
    created() {
        let vm = this;
        this.menu.loadGrantedItems(function() {
            vm.disabled = !vm.menu.isGranted(vm.path);
        });
    },
    methods: {
        click() {
            if (!this.disabled && this.item && this.$router) {
                if (this.item.path) {
                    let vm = this;
                    this.$router.push(this.path).catch(function() {
                        // 指定路径无法跳转，则触发点击事件
                        vm.$emit('click');
                    });
                } else { // 匹配菜单项未配置路径，则触发点击事件
                    this.$emit('click');
                }
            }
        }
    }
}
</script>
