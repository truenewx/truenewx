<template>
    <el-button type="danger" @click="close()" v-if="text">{{ text }}</el-button>
</template>

<script>
export default {
    name: 'TnxelCloseErrorButton',
    data() {
        return {
            backable: window.history.length > 0,
            closeable: window.opener !== undefined,
        };
    },
    computed: {
        text() {
            if (this.backable) {
                return '返回';
            }
            if (this.closeable) {
                return '关闭';
            }
            return undefined;
        },
    },
    methods: {
        close() {
            if (this.backable) {
                window.history.back();
            } else if (this.closeable) {
                window.close();
            }
        },
    }
}
</script>
