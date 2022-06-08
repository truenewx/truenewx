<template>
    <el-steps class="w-fit-content tnxel-steps-nav" :id="id" direction="vertical" :space="space">
        <el-step status="finish" v-for="item in items" :key="item.target" :data-target="item.target"
            :title="item.title"/>
    </el-steps>
</template>

<script>
import $ from 'jquery';

export default {
    name: 'TnxelStepsNav',
    props: {
        /**
         * 所属滚动容器的选择器
         */
        container: {
            type: String,
            default: () => 'main',
        },
        items: {
            type: Array,
            required: true,
        },
        base: [String, Object],
        space: [String, Number],
    },
    data() {
        return {
            id: window.tnx.util.string.random(32),
            activeIndex: 0,
            topOffset: 0,
        }
    },
    mounted() {
        this.topOffset = $('#' + this.id).offset().top - $(this.container).offset().top - 16;
        let vm = this;
        $('#' + this.id + ' .el-step').each(function(index, step) {
            $(step).click(function() {
                vm.navTo(index);
            });
        });
    },
    methods: {
        navTo(index) {
            this.activeIndex = index;
            let top = 0;
            if (index >= 0) {
                let $base = this.base ? $(this.base) : $('#' + this.items[0].target);
                let $target = $('#' + this.items[index].target);
                top = $target.offset().top - $base.offset().top + this.topOffset;
            }
            $(this.container).scrollTop(top);
        },
    }
}
</script>
