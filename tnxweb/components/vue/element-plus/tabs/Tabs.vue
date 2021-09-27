<template>
    <el-tabs class="tnxel-tabs" :id="id" :type="type" v-model="model" @tab-click="clickTab">
        <slot v-if="$slots.default"></slot>
        <template v-else>
            <el-tab-pane :name="pane.name" v-for="pane of panes" :key="pane.name">
                <template #label>
                    <span class="tnxel-tabs-pane-label">{{ pane.label }}</span>
                </template>
            </el-tab-pane>
        </template>
    </el-tabs>
</template>

<script>
export default {
    name: 'TnxelTabs',
    props: {
        type: String,
        modelValue: String,
        items: Object,
        id: {
            type: String,
            default: 'tnxel-tabs',
        },
    },
    emits: ['update:modelValue'],
    data() {
        return {
            model: this.modelValue,
        }
    },
    computed: {
        panes() {
            let panes = [];
            if (this.items) {
                let keys = Object.keys(this.items);
                for (let key of keys) {
                    let label = this.items[key];
                    if (typeof label === 'string') {
                        panes.push({
                            name: key,
                            label: label,
                        });
                    }
                }
            }
            return panes;
        }
    },
    watch: {
        model(model) {
            let cache = this.getCache();
            if (cache) {
                cache[this.id] = model;
            }
            this.$emit('update:modelValue', model);
        },
        modelValue(value) {
            this.model = value;
        }
    },
    mounted() {
        let cache = this.getCache();
        if (cache && this.$route.meta.isHistory()) {
            let model = cache[this.id];
            if (model) {
                let vm = this;
                this.$nextTick(function() {
                    vm.model = model;
                });
            }
        }
    },
    methods: {
        getCache() {
            if (this.$route && this.$route.meta) {
                let cache = this.$route.meta.cache.tabs;
                if (!cache) {
                    cache = {};
                    this.$route.meta.cache.tabs = cache;
                }
                return cache;
            }
            return undefined;
        },
        clickTab(tab, event) {
            this.$emit('tab-click', tab, event);
        }
    }
}
</script>
