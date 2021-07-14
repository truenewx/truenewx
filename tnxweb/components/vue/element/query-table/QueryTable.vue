<template>
    <div class="tnxel-query-table">
        <el-table :data="records" :empty-text="emptyRecordText" :border="border" :stripe="stripe"
            @sort-change="sort" :default-sort="defaultSort" :key="defaultSort" :row-class-name="rowClassName">
            <slot></slot>
        </el-table>
        <tnxel-paged :value="paged" :change="query" :align="pagedAlign" v-if="paged"/>
    </div>
</template>

<script>
import Paged from '../paged';

export default {
    components: {
        'tnxel-paged': Paged
    },
    name: 'TnxelQueryTable',
    props: {
        app: String,
        url: {
            type: String,
            required: true,
        },
        value: Object,
        border: {
            type: Boolean,
            default: true,
        },
        stripe: {
            type: Boolean,
            default: true,
        },
        pagedAlign: String,
        success: Function,
        rowClassName: String,
        formatter: Function,
    },
    data() {
        return {
            params: this.getParams(this.value),
            records: null,
            querying: false,
            paged: null,
        }
    },
    computed: {
        emptyRecordText() {
            if (this.querying) {
                return '加载中...';
            } else if (this.records === null) {
                return '尚未开始查询';
            } else {
                return '<空>';
            }
        },
        defaultSort() {
            if (this.paged && this.paged.orders && this.paged.orders.length) {
                let fieldOrder = this.paged.orders[0];
                return {
                    prop: fieldOrder.name,
                    order: fieldOrder.desc ? 'descending' : 'ascending',
                };
            }
            if (this.params && this.params.orderBy) {
                let array = this.params.orderBy.split(' ');
                return {
                    prop: array[0],
                    order: (array[1] || 'asc').toLowerCase() === 'desc' ? 'descending' : 'ascending',
                }
            }
            return undefined;
        },
    },
    watch: {
        value(value) {
            this.params = this.getParams(value);
        }
    },
    methods: {
        getParams(value) {
            return Object.assign({}, value); // 避免改动传入的参数对象
        },
        query(params) {
            if (typeof params === 'number') { // 参数为页码
                this.params.pageNo = params;
                if (this.value) { // 指定了value属性，在页码变更时需要触发更新事件
                    this.$emit('input', this.params);
                }
            } else if (typeof params === 'object') {
                this.params = this.getParams(params);
                this.params.pageNo = this.params.pageNo || 1;
            }

            this.records = null;
            this.querying = true;
            let vm = this;
            window.tnx.app.rpc.get(this.url, this.params, function(result) {
                vm.querying = false;
                if (Array.isArray(result)) {
                    vm.records = vm.format(result);
                } else {
                    vm.records = vm.format(result.records);
                    vm.paged = result.paged;
                }
                if (vm.success) {
                    vm.success(vm.records, vm.paged);
                }
            }, {
                app: this.app
            });
        },
        format(records) {
            if (this.formatter) {
                let result = [];
                for (let record of records) {
                    let obj = this.formatter(record);
                    if (obj) {
                        result.push(obj);
                    }
                }
                return result;
            }
            return records;
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
