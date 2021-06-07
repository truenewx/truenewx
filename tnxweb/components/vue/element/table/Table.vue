<template>
    <div class="tnxel-table">
        <el-table :data="records" :empty-text="emptyRecordText" :border="border" :stripe="stripe"
            @sort-change="sort" :default-sort="defaultSort" :key="defaultSort">
            <slot></slot>
        </el-table>
        <tnxel-paged :value="paged" :change="query" :align="pagedAlign"/>
    </div>
</template>

<script>
import Paged from '../paged';

export default {
    components: {
        'tnxel-paged': Paged
    },
    name: 'TnxelTable',
    props: {
        app: String,
        url: {
            type: String,
            required: true,
        },
        border: {
            type: Boolean,
            default: true,
        },
        stripe: {
            type: Boolean,
            default: true,
        },
        defaultParams: {
            type: [Object, Boolean],
            default: true
        },
        pagedAlign: String,
    },
    data() {
        return {
            params: {
                pageSize: 20,
                pageNo: 1,
            },
            records: null,
            paged: {},
        }
    },
    computed: {
        emptyRecordText() {
            return this.records === null ? '加载中...' : '<空>';
        },
        defaultSort() {
            if (this.paged && this.paged.orders && this.paged.orders.length) {
                let fieldOrder = this.paged.orders[0];
                return {
                    prop: fieldOrder.name,
                    order: fieldOrder.desc ? 'descending' : 'ascending',
                };
            }
            return undefined;
        },
    },
    created() {
        if (this.defaultParams !== false) {
            if (typeof this.defaultParams === 'object') {
                this.query(this.defaultParams);
            } else {
                this.query();
            }
        } else {
            this.records = [];
        }
    },
    methods: {
        query(params) {
            if (typeof params === 'number') { // 参数为页码
                this.params.pageNo = params;
            } else if (typeof params === 'object') {
                this.params = params;
                this.params.pageSize = this.params.pageSize || 20;
                this.params.pageNo = this.params.pageNo || 1;
            }
            this.records = null;
            let vm = this;
            window.tnx.app.rpc.get(this.url, this.params, function(result) {
                vm.records = result.records;
                vm.paged = result.paged;
            }, {
                app: this.app
            });
        },
        sort(options) {
            if (options && options.prop && options.order) {
                this.params.orderBy = options.prop + (options.order === 'descending' ? ' desc' : '');
            } else {
                delete this.params.orderBy;
            }
            this.query(1);
        }
    }
}
</script>
