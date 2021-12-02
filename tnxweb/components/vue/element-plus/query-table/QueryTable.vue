<template>
    <div class="tnxel-query-table" :class="{selectable: selectable}">
        <el-table ref="table" :data="records" :empty-text="emptyRecordText" :size="size" :border="border"
            :stripe="stripe" @sort-change="sort" :default-sort="defaultSort" :key="defaultSort"
            :row-class-name="rowClassName" @cell-click="selectRow">
            <el-table-column class="select-column" header-align="center" align="center" width="50px" v-if="selectable">
                <template #header>
                    <el-checkbox :model-value="pageAllSelected" :indeterminate="allSelectedIndeterminate"
                        @change="selectAll" v-if="selectable === 'all'"/>
                    <span v-else>选择</span>
                </template>
                <template #default="scope">
                    <el-checkbox v-model="pageSelectedIndexes[scope.$index]" @change="selectPageToAll"
                        v-if="selectable === 'multi' || selectable === 'all'"/>
                </template>
            </el-table-column>
            <slot></slot>
        </el-table>
        <slot name="paged" :paged="paged" :show="showPaged" :query="query" v-if="paged">
            <tnxel-paged :value="paged" :change="onPagedChange" :align="pagedAlign" v-if="showPaged"/>
        </slot>
    </div>
</template>

<script>
import Paged from '../paged/Paged';

export default {
    components: {
        'tnxel-paged': Paged
    },
    name: 'TnxelQueryTable',
    props: {
        app: String,
        url: String,
        data: Object, // QueryResult
        modelValue: Object,
        size: String,
        border: {
            type: Boolean,
            default: true,
        },
        stripe: {
            type: Boolean,
            default: true,
        },
        showPaged: {
            type: Boolean,
            default: true,
        },
        pagedAlign: String,
        success: Function,
        rowClassName: String,
        formatter: Function,
        order: Function,
        pagedChange: Function,
        selectable: { // 是否可选择
            type: [Boolean, String], // false-不可选择；true/'single'-可单选；'multi'-可多选但不可全选；'all'-可多选且可全选
            default() {
                return false;
            }
        },
        selectName: { // 比较已选对象与表格数据中行对象是否相等的字段名称，这要求数据对象必须具有唯一标识字段
            type: String,
            default() {
                return 'id';
            }
        },
        selected: [Object, Array], // 已选择的行对象
    },
    emits: ['update:modelValue', 'update:selected'],
    data() {
        return {
            params: this.getParams(this.modelValue),
            records: this.data ? this.data.records : null,
            querying: false,
            paged: this.data ? this.data.paged : null,
            pageSelectedIndexes: [], // 当前页已选择记录的索引
            allSelectedRecords: this.selected || [], // 所有已选择的记录
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
            let sortableColumnNames = [];
            if (this.$slots.default) {
                let columns = this.$slots.default();
                for (let column of columns) {
                    if (column.props.prop && column.props.sortable === 'custom') {
                        sortableColumnNames.push(column.props.prop);
                    }
                }
            }
            if (sortableColumnNames.length) {
                if (this.paged && this.paged.orders && this.paged.orders.length) {
                    let fieldOrder = this.paged.orders[0];
                    let fieldName = fieldOrder.name;
                    if (sortableColumnNames.contains(fieldName)) {
                        return {
                            prop: fieldName,
                            order: fieldOrder.desc ? 'descending' : 'ascending',
                        };
                    }
                }
                if (this.params && this.params.orderBy) {
                    let array = this.params.orderBy.split(' ');
                    let fieldName = array[0];
                    if (sortableColumnNames.contains(fieldName)) {
                        return {
                            prop: fieldName,
                            order: (array[1] || 'asc').toLowerCase() === 'desc' ? 'descending' : 'ascending',
                        }
                    }
                }
            }
            return undefined;
        },
        pageAllSelected() {
            if (this.records?.length) {
                let selected = null;
                for (let i = 0; i < this.records.length; i++) {
                    if (selected == null) {
                        selected = this.pageSelectedIndexes[i];
                    } else if (selected !== this.pageSelectedIndexes[i]) {
                        return null;
                    }
                }
                return selected;
            }
            return false;
        },
        allSelectedIndeterminate() {
            if (this.records?.length) {
                let firstSelected = this.pageSelectedIndexes[0];
                for (let i = 1; i < this.records.length; i++) {
                    if (this.pageSelectedIndexes[i] !== firstSelected) {
                        return true;
                    }
                }
            }
            return false;
        }
    },
    watch: {
        modelValue(value) {
            this.params = this.getParams(value);
        },
        allSelectedRecords() {
            this.$emit('update:selected', this.allSelectedRecords);
        },
    },
    methods: {
        getParams(modelValue) {
            return Object.assign({}, modelValue); // 避免改动传入的参数对象
        },
        onPagedChange(pageNo) {
            if (this.pagedChange && this.pagedChange(pageNo) === false) {
                return;
            }
            this.query(pageNo);
        },
        query(params) {
            if (typeof params === 'number') { // 参数为页码
                this.params.pageNo = params;
                if (this.modelValue) { // 指定了modelValue属性，在页码变更时需要触发更新事件
                    this.$emit('update:modelValue', this.params);
                }
            } else if (typeof params === 'object') {
                this.params = this.getParams(params);
                this.params.pageNo = this.params.pageNo || 1;
                // 带查询条件参数对象的为全新查询，清空已选清单
                this.pageSelectedIndexes = [];
                this.allSelectedRecords = [];
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
                vm.selectAllToPage();
                if (vm.success) {
                    vm.success(vm.records, vm.paged);
                }
            }, {
                app: this.app
            });
        },
        format(records) {
            if (this.formatter) {
                return this.formatter(records);
            }
            return records;
        },
        sort(options) {
            if (options && options.prop && options.order) {
                this.params.orderBy = options.prop + (options.order === 'descending' ? ' desc' : '');
            } else {
                delete this.params.orderBy;
            }
            this.params.pageNo = 1;
            if (this.modelValue) {
                this.$emit('update:modelValue', this.params);
            }
            if (this.order && this.order(this.params.orderBy) === false) {
                return;
            }
            this.query();
        },
        selectRow(row, column, cell) {
            if (this.selectable && this.records && column.getColumnIndex() > 0 && !cell.innerHTML.contains('</a>')) {
                let vm = this;
                let index = window.tnx.util.array.indexOf(this.records, function(element) {
                    return row[vm.selectName] === element[vm.selectName];
                });
                if (index >= 0) {
                    this.pageSelectedIndexes[index] = !this.pageSelectedIndexes[index];
                    this.selectPageToAll();
                }
            }
        },
        selectAll(selected) {
            if (this.records?.length) {
                for (let i = 0; i < this.records.length; i++) {
                    this.pageSelectedIndexes[i] = selected;
                }
                this.selectPageToAll();
            }
        },
        selectPageToAll() {
            let vm = this;
            for (let index = 0; index < this.pageSelectedIndexes.length; index++) {
                let selectedInPage = this.pageSelectedIndexes[index];
                let record = this.records[index];
                let fnEquals = function(element) {
                    return record[vm.selectName] === element[vm.selectName];
                };
                let selectedInAll = this.allSelectedRecords.contains(fnEquals);
                if (selectedInPage && !selectedInAll) { // 当前页已选但全局未选，则加入全局已选清单
                    this.allSelectedRecords.push(record);
                } else if (!selectedInPage && selectedInAll) { // 当前页未选但全局已选，则从全局已选中移除
                    this.allSelectedRecords.remove(fnEquals);
                }
            }
            this.$emit('update:selected', this.allSelectedRecords);
        },
        selectAllToPage() {
            if (this.selectable) {
                let vm = this;
                if (!Array.isArray(this.allSelectedRecords)) {
                    this.allSelectedRecords = [this.allSelectedRecords];
                }
                this.pageSelectedIndexes = [];
                for (let selectedRecord of this.allSelectedRecords) {
                    for (let i = 0; i < this.records.length; i++) {
                        let record = this.records[i];
                        if (record[vm.selectName] === selectedRecord[vm.selectName]) {
                            this.pageSelectedIndexes[i] = true;
                        }
                    }
                }
            }
        },
    }
}
</script>

<style>
.tnxel-query-table.selectable .el-table__row {
    cursor: pointer;
}

.tnxel-query-table.selectable .el-checkbox {
    height: auto;
}

.tnxel-query-table.selectable .el-table__cell:first-child .cell {
    display: flex;
    align-items: center;
    justify-content: center;
}

</style>
