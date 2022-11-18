<template>
    <el-button type="danger" @click="close()" v-if="text">{{ text }}</el-button>
</template>

<script>
export default {
    name: 'TnxelCloseErrorButton',
    props: {
        prev: String,
    },
    data() {
        return {};
    },
    computed: {
        backable() {
            return this.prev || window.history.length > 2; // 前两个为新标签页和当前页，需排除
        },
        closeable() {
            return window.opener !== undefined && window.opener !== null;
        },
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
                if (this.prev) {
                    window.location.href = this.prev;
                } else {
                    window.history.back();
                }
            } else if (this.closeable) {
                window.close();
            }
        },
    }
}
</script>
