<template>
    <div class="tnxel-pagination-container"
        :class="{'justify-content-center': align === 'center', 'justify-content-end': align === 'right'}" v-if="model">
        <slot :paged="value" :change="change" :background="background">
            <el-pagination layout="total, slot, prev, pager, next" :background="background" @current-change="change"
                v-model:page-size="model.pageSize" v-model:current-page="model.pageNo" :total="model.total || 0">
                <span class="el-pagination__page-size" v-if="pageSizeItems.length <= 1">{{ model.pageSize }}条/页</span>
                <tnxel-select class="el-pagination__page-size" v-model="model.pageSize" :items="pageSizeItems" v-else/>
            </el-pagination>
        </slot>
    </div>
</template>

<script>
import Select from '../select/Select';

export default {
    name: 'TnxelPaged',
    components: {
        'tnxel-select': Select,
    },
    props: {
        value: Object,
        change: {
            type: Function
        },
        background: {
            type: Boolean,
            default: true,
        },
        align: String,
        pageSizes: Array,
    },
    data() {
        return {
            model: this.value,
        }
    },
    watch: {
        value() {
            this.model = this.value;
        }
    },
    computed: {
        pageSizeItems() {
            let pageSizeItems = [];
            if (this.pageSizes) {
                for (let pageSize of this.pageSizes) {
                    pageSizeItems.push({
                        value: pageSize,
                        text: pageSize + '条/页',
                    });
                }
            }
            return pageSizeItems;
        }
    },
}
</script>

<style>
.tnxel-pagination-container {
    display: flex;
    padding: 0.5rem 0;
}

.tnxel-pagination-container .el-pagination {
    padding: 0;
}

.tnxel-pagination-container .el-pagination__page-size {
    color: var(--el-text-color-regular);
    margin-right: 1rem;
}
</style>
