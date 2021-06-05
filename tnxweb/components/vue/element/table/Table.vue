<template>
    <div>
        <el-table :data="records" :empty-text="emptyRecordText" :border="border" :stripe="stripe"
            @sort-change="sort" :default-sort="defaultSort" :key="defaultSort">
            <slot></slot>
        </el-table>
        <tnxel-paged :value="paged" :change="query"/>
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
        params: [Object, Function],
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
        app: String,
    },
    data() {
        return {
            paging: this.getPaging(this.params),
            records: null,
            paged: {},
        }
    },
    watch: {
        params(params) {
            this.paging = this.getPaging(params);
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
        this.query();
    },
    methods: {
        getPaging(params) {
            if (typeof params === 'function') {
                params = params();
            }
            return Object.assign({
                pageNo: 1
            }, params);
        },
        query(pageNo) {
            if (pageNo) {
                this.paging.pageNo = pageNo;
            }
            this.records = null;
            const app = window.tnx.app;
            let vm = this;
            app.rpc.get(this.url, this.paging, function(result) {
                vm.records = result.records;
                vm.paged = result.paged;
            }, {
                app: this.app
            });
        },
        sort(options) {
            if (options && options.prop && options.order) {
                this.paging.orderBy = options.prop + (options.order === 'descending' ? ' desc' : '');
            } else {
                delete this.paging.orderBy;
            }
            this.query(1);
        }
    }
}
</script>
