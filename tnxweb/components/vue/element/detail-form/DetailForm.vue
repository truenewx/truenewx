<template>
    <el-form :label-position="vertical ? 'top' : 'right'" label-width="auto" ref="form" :model="model"
        class="tnxel-detail-form" :class="formClass">
        <slot></slot>
        <el-form-item class="w-100" v-if="update || back !== false">
            <el-button :type="theme || 'primary'" @click="toUpdate" v-if="update">{{ updateText }}</el-button>
            <el-button type="default" @click="toBack" v-if="back !== false">{{ backText }}</el-button>
        </el-form-item>
    </el-form>
</template>

<script>
export default {
    name: 'TnxelDetailForm',
    props: {
        model: {
            type: Object,
            default: () => {
            },
        },
        theme: String,
        update: {
            type: Function,
        },
        updateText: {
            type: String,
            default: () => '修改'
        },
        back: {
            type: [String, Function, Boolean],
            default: () => true
        },
        backText: {
            type: String,
            default: () => '返回'
        },
        vertical: {
            type: Boolean,
            default: () => false
        },
        columns: {
            type: Number,
            default: 1,
        }
    },
    data() {
        return {};
    },
    computed: {
        formClass() {
            let formClass = '';
            if (this.theme) {
                formClass += ' theme-' + this.theme;
            }
            if (this.columns > 1 && this.columns < 5) {
                formClass += ' form-columns-' + this.columns;
            }
            return formClass.trim();
        }
    },
    methods: {
        toUpdate() { // 为了避免传递事件参数，不直接使用update()
            if (this.update) {
                this.update();
            }
        },
        toBack() {
            if (typeof this.back === 'function') {
                this.back();
            } else if (typeof this.back === 'string') {
                this.$router.back(this.back);
            } else if (this.back !== false) {
                this.$router.back();
            }
        }
    }
}
</script>
