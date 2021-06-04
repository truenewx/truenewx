<template>
    <el-col :span="span">
        <el-button :type="btnType" :icon="btnIcon" @click="toAdd" v-if="addable">{{ addText }}</el-button>
        <el-table :data="list" border stripe v-if="showEmpty || (list && list.length)">
            <slot></slot>
            <el-table-column label="操作" header-align="center" align="center" width="100px"
                v-if="updatable || removeable">
                <template slot-scope="scope">
                    <el-button type="text" class="p-0" @click="toUpdate(scope.$index)" v-if="updatable">
                        {{ updateText }}
                    </el-button>
                    <el-button type="text" class="p-0" @click="toRemove(scope.$index)" v-if="removeable">
                        {{ removeText }}
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-col>
</template>

<script>
export default {
    name: 'TnxelCurd',
    props: {
        page: {
            type: Object,
            required: true,
        },
        pageProps: Object,
        value: Array,
        modelName: String,
        span: Number,
        btnType: {
            type: String,
            default: 'primary',
        },
        btnIcon: String,
        addable: {
            type: Boolean,
            default: true,
        },
        addText: {
            type: String,
            default: '新增',
        },
        add: Function,
        updatable: {
            type: Boolean,
            default: true,
        },
        updateText: {
            type: String,
            default: '修改',
        },
        update: Function,
        removeable: {
            type: Boolean,
            default: true,
        },
        removeText: {
            type: String,
            default: '移除',
        },
        remove: Function,
        formatter: Function,
        order: [String, Function],
        showEmpty: {
            type: Boolean,
            default: false,
        }
    },
    data() {
        return {
            list: this.value,
        }
    },
    watch: {
        list() {
            this.format();
            this.$emit('input', this.list);
        },
        value(value) {
            this.list = value;
            this.format();
        }
    },
    methods: {
        format() {
            if (this.formatter && this.list && this.list.length) {
                for (let i = 0; i < this.list.length; i++) {
                    this.list[i] = this.formatter(this.list[i]);
                }
            }
        },
        toAdd() {
            let vm = this;
            window.tnx.open(this.page, this.pageProps, {
                title: vm.addText + (vm.modelName || ''),
                click: function(yes, close) {
                    if (yes) {
                        if (typeof this.validateForm === 'function') {
                            this.validateForm(function(model) {
                                if (vm.add) {
                                    vm.add(model, function() {
                                        vm._onAdded(model, close);
                                    });
                                } else {
                                    vm._onAdded(model, close);
                                }
                            });
                            return false;
                        }
                    }
                }
            });
        },
        _onAdded(model, close) {
            this.list = this.list || [];
            this.list.push(model);
            this._sort();
            close();
        },
        _sort() {
            let sort = this.order;
            if (typeof sort === 'string') {
                let array = sort.split(' ');
                let orderBy = array[0].trim();
                let desc = array[1] === 'desc';
                sort = function(o1, o2) {
                    let v1 = o1[orderBy];
                    let v2 = o2[orderBy];
                    if (v1 < v2) {
                        return desc ? 1 : -1;
                    } else if (v1 === v2) {
                        return 0;
                    } else {
                        return desc ? -1 : 1;
                    }
                }
            }
            if (typeof sort === 'function') {
                this.list.sort(sort);
            }
        },
        toUpdate(index) {
            let model = Object.assign({}, this.list[index]);
            if (model) {
                let vm = this;
                window.tnx.open(this.page, {
                    value: model
                }, {
                    title: vm.updateText + (vm.modelName || ''),
                    click: function(yes, close) {
                        if (yes) {
                            if (typeof this.validateForm === 'function') {
                                this.validateForm(function(model) {
                                    if (vm.update) {
                                        vm.update(index, model, function() {
                                            vm._onUpdated(index, model, close);
                                        });
                                    } else {
                                        vm._onUpdated(index, model, close);
                                    }
                                });
                                return false;
                            }
                        }
                    }
                });
            }
        },
        _onUpdated(index, model, close) {
            Object.assign(this.list[index], model);
            this._sort();
            close();
        },
        toRemove(index) {
            let vm = this;
            let modelName = this.modelName ? ('该' + this.modelName) : '';
            window.tnx.confirm('确定要' + this.removeText + modelName + '吗？', function(yes) {
                if (yes) {
                    if (vm.remove) {
                        vm.remove(index, function() {
                            vm._onRemoved(index);
                        });
                    } else {
                        vm._onRemoved(index);
                    }
                }
            });
        },
        _onRemoved(index) {
            this.list.splice(index, 1);
        }
    }
}
</script>
