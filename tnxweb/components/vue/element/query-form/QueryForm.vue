<template>
    <el-form :inline="true" :model="value" :class="theme ? ('theme-' + theme) : null">
        <slot></slot>
        <el-form-item v-if="query">
            <el-button :type="theme || 'primary'" icon="el-icon-search" @click="toQuery" :plain="plain">
                {{ queryText }}
            </el-button>
            <el-button @click="toClear" :plain="plain" v-if="clearable">{{ clearText }}</el-button>
        </el-form-item>
    </el-form>
</template>

<script>
export default {
    name: 'TnxelQueryForm',
    props: {
        value: {
            type: Object,
            required: true,
        },
        theme: String,
        query: Function,
        queryText: {
            type: String,
            default: () => '查询'
        },
        clearable: {
            type: Boolean,
            default: () => true
        },
        clearText: {
            type: String,
            default: () => '清空'
        },
        clear: Function,
        plain: {
            type: Boolean,
            default: () => true
        }
    },
    methods: {
        toQuery() { // 为了避免传递事件参数，不直接使用query()
            if (this.query) {
                this.query();
            }
        },
        toClear() {
            if (Object.keys(this.value).length) {
                this.$emit('input', {});
                if (this.clear) {
                    if (this.clear() === false) {
                        return;
                    }
                }
                let parameters = window.tnx.util.net.getParameters();
                if (Object.keys(parameters).length) {
                    this.$router.replace(this.$route.path);
                } else {
                    this.toQuery();
                }
            }
        }
    }
}
</script>
