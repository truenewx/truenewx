<template>
    <div class="tnxel-tag-select">
        <el-input prefix-icon="el-icon-search" :placeholder="'可输入' + keywordCaption + '进行筛选'"
            :clearable="true" v-model="keyword" v-if="paged"/>
        <div class="tnxel-tag-group d-flex flex-wrap" v-if="tags">
            <el-tag v-for="tag in tags" :key="tag.key" :type="theme" :size="tagSize"
                :effect="isSelected(tag.key) ? 'dark' : 'plain'" @click="select(tag.key)">
                {{ tag.text }}
            </el-tag>
            <div class="d-flex justify-content-between" v-if="paged && paged.pageCount > 1">
                <div class="el-pagination">
                    <span class="el-pagination__total">已选择 {{ selectedKeys.length }} 个</span>
                </div>
                <el-pagination layout="prev, pager, next" background @current-change="query"
                    :total="paged.total" :page-size="paged.pageSize" :current-page="paged.pageNo"/>
            </div>
        </div>
        <div v-else>
            <i class="el-icon-loading"/>
        </div>
    </div>
</template>

<script>
export default {
    name: 'TnxelTagSelect',
    props: {
        tnx: {
            type: Object,
            default() {
                return window.tnx;
            }
        },
        type: String,
        subtype: String,
        theme: String,
        tagSize: String,
        items: [Array, String],
        pageSize: Number,
        click: Function,
        keyName: {
            type: String,
            default: 'key',
        },
        textName: {
            type: String,
            default: 'text',
        },
        keywordName: {
            type: String,
            default: 'keyword',
        },
        keywordCaption: {
            type: String,
            default: '关键字',
        },
        keys: {
            type: Array,
            default() {
                return [];
            }
        },
        toTag: {
            type: Function,
            default(item) {
                return {
                    key: item[this.keyName],
                    text: item[this.textName],
                };
            }
        }
    },
    data() {
        return {
            tags: null,
            selectedKeys: this.keys,
            paged: null,
            keyword: '',
        }
    },
    watch: {
        items() {
            this.query();
        },
        keys() {
            this.selectedKeys = this.keys;
        },
        keyword() {
            this.query();
        },
    },
    created() {
        let vm = this;
        if (vm.type && typeof vm.type === 'string') { // 如果传了type则优先加载枚举
            window.tnx.app.rpc.loadEnumItems(vm.type, vm.subtype, function(items) {
                vm.textName = "caption";
                vm.toTags(items);
            });
        } else {
            this.query();
        }
    },
    methods: {
        query(pageNo) {
            if (typeof this.items === 'string') {
                const vm = this;
                const params = {};
                if (this.pageSize) {
                    params.pageSize = this.pageSize;
                }
                if (typeof pageNo === 'number') {
                    params.pageNo = pageNo;
                }
                if (this.paged) {
                    params[this.keywordName] = this.keyword;
                }
                this.tnx.app.rpc.get(this.items, params, result => {
                    if (result instanceof Array) {
                        vm.toTags(result);
                    } else if (result.records && result.paged) {
                        vm.toTags(result.records);
                        vm.paged = result.paged;
                    }
                });
            } else if (this.items) {
                this.toTags(this.items);
            }
        },
        toTags(items) {
            const tags = [];
            const vm = this;
            items.forEach(item => {
                tags.push(vm.toTag(item));
            });
            this.tags = tags;
        },
        isSelected(key) {
            return this.selectedKeys.contains(key);
        },
        select(key) {
            if (!this.click || this.click(key)) {
                const selectedKeyIndex = this.selectedKeys.indexOf(key);
                if (selectedKeyIndex >= 0) {
                    this.selectedKeys.splice(selectedKeyIndex, 1);
                } else {
                    this.selectedKeys.push(key);
                }
            }

        },
        getSelectedKeys() {
            return this.selectedKeys;
        }
    }
}
</script>

<style scoped>
.tnxel-tag-select .el-input {
    margin-bottom: 5px;
    width: 210px;
}

.tnxel-tag-select .tnxel-tag-group .el-tag {
    margin-top: 5px;
    margin-bottom: 5px;
    cursor: pointer;
}

.tnxel-tag-select .tnxel-tag-group .el-tag:not(:last-child) {
    margin-right: 10px;
}

.tnxel-tag-select .el-pagination {
    margin-top: 5px;
    line-height: 28px;
}

.tnxel-tag-select .el-pagination .el-pager {
    line-height: 28px;
}
</style>
