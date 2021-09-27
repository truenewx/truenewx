<template>
    <div class="tnxel-tag-select" v-if="items">
        <el-input v-model="keyword" prefix-icon="el-icon-search" :placeholder="placheholder" clearable
            v-if="filterable"/>
        <tnxel-select v-model="model" :selector="multi ? 'tags' : 'tag'" :items="items" :value-name="valueName"
            :text-name="textName" :change="change" :empty="empty" :theme="theme" :size="size">
            <template #empty>
                <slot name="empty"></slot>
            </template>
        </tnxel-select>
        <div class="d-flex justify-content-between" v-if="paged && paged.pageCount > 1">
            <div class="el-pagination" v-if="multi">
                <span class="el-pagination__total">已选择 {{ value.length }} 个</span>
            </div>
            <el-pagination layout="prev, pager, next" background @current-change="query"
                :total="paged.total" :page-size="paged.pageSize" :current-page="paged.pageNo"/>
        </div>
    </div>
</template>

<script>
import Select from '../select/Select';

export default {
    name: 'TnxelFetchTags',
    components: {
        'tnxel-select': Select,
    },
    props: {
        modelValue: [String, Number, Boolean, Array],
        url: {
            type: String,
            required: true,
        },
        params: [Object, Function],
        valueName: {
            type: String,
            default: 'id',
        },
        textName: {
            type: String,
            default: 'name',
        },
        keywordName: {
            type: String,
            default: 'keyword',
        },
        multi: Boolean,
        empty: String,
        filterable: Boolean,
        placheholder: String,
        change: Function,
        theme: String,
        size: String,
        formatter: Function,
    },
    emits: ['update:modelValue'],
    data() {
        return {
            model: this.modelValue,
            items: null,
            paged: null,
            keyword: '',
        }
    },
    watch: {
        model(value) {
            this.$emit('update:modelValue', value);
        },
        modelValue(value) {
            this.model = value;
        },
        url() {
            this.query();
        },
        keyword() {
            this.query();
        },
    },
    created() {
        this.query();
    },
    methods: {
        query(pageNo) {
            let params;
            if (typeof this.params === 'function') {
                params = this.params(pageNo);
            } else {
                params = Object.assign({}, this.params);
                if (pageNo) {
                    params.pageNo = pageNo;
                }
            }
            if (this.keyword) {
                params[this.keywordName] = this.keyword;
            }
            let vm = this;
            window.tnx.app.rpc.get(this.url, params, result => {
                if (result instanceof Array) {
                    vm.items = vm.format(result);
                } else if (result.records && result.paged) {
                    vm.items = vm.format(result.records);
                    vm.paged = result.paged;
                }
            });
        },
        format(items) {
            let result = items;
            if (items && this.formatter) {
                result = [];
                for (let item of items) {
                    item = this.formatter(item);
                    if (item) {
                        result.push(item);
                    }
                }
            }
            return result;
        }
    }
}
</script>
