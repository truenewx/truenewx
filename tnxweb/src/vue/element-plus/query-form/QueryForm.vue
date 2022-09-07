<template>
    <el-form :id="id" :inline="inline" :model="params" :class="theme ? ('theme-' + theme) : null">
        <slot></slot>
        <el-form-item class="me-0" v-if="queryText || clearText">
            <tnxel-button :type="theme || 'primary'" icon="Search" @click="toQuery" :plain="plain"
                v-if="queryText">
                {{ queryText }}
            </tnxel-button>
            <el-button @click="toClear" :plain="plain" title="清空查询条件" v-if="clearText">{{ clearText }}</el-button>
        </el-form-item>
    </el-form>
</template>

<script>
import {ObjectUtil} from '../../../tnxcore-util';
import Button from '../button/Button';

export default {
    name: 'TnxelQueryForm',
    components: {
        'tnxel-button': Button,
    },
    props: {
        id: String,
        modelValue: {
            type: Object,
            default() {
                return {};
            },
        },
        theme: String,
        query: Function,
        queryText: {
            type: String,
            default: '查询',
        },
        clearText: {
            type: String,
            default: '清空',
        },
        clear: Function,
        plain: {
            type: Boolean,
            default: true,
        },
        init: { // 是否初始化执行查询
            type: Boolean,
            default: false,
        },
        inline: {
            type: Boolean,
            default: true,
        }
    },
    emits: ['update:modelValue'],
    data() {
        return {
            params: this.modelValue,
        }
    },
    computed: {
        cacheKey() {
            let key = this.$options.name;
            if (this.id) {
                key += '-' + this.id;
            }
            return key;
        }
    },
    watch: {
        modelValue() {
            this.params = this.modelValue;
            this.cacheParams();
        }
    },
    mounted() {
        let vm = this;
        setTimeout(function() {
            let queryable = vm.init;
            if (vm.$route.meta.isHistory()) {
                queryable = true; // 历史性访问均需要执行查询
                let params = vm.$route.meta.cache[vm.cacheKey];
                if (params) {
                    let keys = Object.keys(vm.params);
                    for (let key of keys) {
                        let value = params[key];
                        if (value !== undefined) {
                            vm.params[key] = value;
                        }
                    }
                }
            }
            if (queryable) {
                vm.toQuery();
            }
        });
    },
    methods: {
        cacheParams() {
            if (typeof this.params.pageNo === 'string') {
                this.params.pageNo = parseInt(this.params.pageNo);
            }
            this.$route.meta.cache[this.cacheKey] = this.params;
        },
        toQuery(event) { // 为了避免传递事件参数，不直接使用query()
            if (this.query) {
                // event不为空，说明方法调用来自于查询按钮点击，此时如果查询参数中有页码，则需重置页码为1
                if (event && this.params.pageNo !== undefined) {
                    this.params.pageNo = 1;
                }
                this.cacheParams();
                this.query();
            }
        },
        toClear() {
            if (Object.keys(this.params).length) {
                if (this.clear) {
                    if (this.clear(this.params) === false) {
                        return;
                    }
                } else {
                    ObjectUtil.clear(this.params, ['pageSize', 'pageNo', 'ignoring']);
                    if (ObjectUtil.isNotNull(this.params.pageNo)) {
                        this.params.pageNo = 1;
                    }
                }
                this.$emit('update:modelValue', this.params);
                let parameters = window.tnx.util.net.getParameters();
                if (Object.keys(parameters).length) {
                    this.$router.replace(this.$route.path);
                } else {
                    this.toQuery();
                }
            }
        }
    }
}
</script>
