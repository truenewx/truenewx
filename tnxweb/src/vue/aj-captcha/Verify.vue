<template>
    <div :class="mode === 'pop' ? 'mask' : ''" v-show="showBox">
        <div :class="mode === 'pop' ? 'verifybox' : ''" :style="{'max-width':parseInt(imgSize.width)+30+'px'}">
            <div class="verifybox-top" v-if="mode === 'pop'">
                请完成安全验证
                <span class="verifybox-close" @click="closeBox">
                <el-icon :size="20">
                    <icon-close/>
                </el-icon>
            </span>
            </div>
            <div class="verifybox-bottom" :style="{padding:mode === 'pop'?'15px':'0'}">
                <!-- 验证码容器 -->
                <component v-if="componentType"
                    :is="componentType"
                    :captchaType="captchaType"
                    :type="verifyType"
                    :figure="figure"
                    :arith="arith"
                    :mode="mode"
                    :vSpace="vSpace"
                    :explain="explain"
                    :imgSize="imgSize"
                    :blockSize="blockSize"
                    :barSize="barSize"
                    ref="instance"></component>
            </div>
        </div>
    </div>
</template>
<script type="text/babel">
/**
 * Verify 验证码组件
 * @description 分发验证码使用
 * */
import VerifySlide from './Verify/VerifySlide'
import VerifyPoints from './Verify/VerifyPoints'
import {computed, ref, toRefs, watchEffect} from 'vue';
import {Close} from '@element-plus/icons-vue';

export default {
    name: 'CaptchaVerify',
    components: {
        VerifySlide,
        VerifyPoints,
        'icon-close': Close,
    },
    props: {
        captchaType: {
            type: String,
            default() {
                return Math.random() < 0.5 ? 'blockPuzzle' : 'clickWord';
            },
        },
        figure: {
            type: Number
        },
        arith: {
            type: Number
        },
        mode: {
            type: String,
            default: 'pop'
        },
        vSpace: {
            type: Number
        },
        explain: {
            type: String
        },
        imgSize: {
            type: Object,
            default() {
                return {
                    width: '310px',
                    height: '155px'
                }
            }
        },
        blockSize: {
            type: Object
        },
        barSize: {
            type: Object
        },
    },
    setup(props) {
        const {captchaType, figure, arith, mode, vSpace, explain, imgSize, blockSize, barSize} = toRefs(props)
        const clickShow = ref(false)
        const verifyType = ref(undefined)
        const componentType = ref(undefined)

        const instance = ref({})

        const showBox = computed(() => {
            if (mode.value === 'pop') {
                return clickShow.value
            } else {
                return true;
            }
        })
        /**
         * refresh
         * @description 刷新
         * */
        const refresh = () => {
            console.log(instance.value);
            if (instance.value.refresh) {
                instance.value.refresh()
            }
        }
        const closeBox = () => {
            clickShow.value = false;
            refresh();
        }
        const show = () => {
            if (mode.value === "pop") {
                clickShow.value = true;
            }
        }
        watchEffect(() => {
            switch (captchaType.value) {
                case 'blockPuzzle':
                    verifyType.value = '2'
                    componentType.value = 'VerifySlide'
                    break
                case 'clickWord':
                    verifyType.value = ''
                    componentType.value = 'VerifyPoints'
                    break
            }
        })

        return {
            clickShow,
            verifyType,
            componentType,
            instance,
            showBox,
            closeBox,
            show
        }
    },
}
</script>
<style>
.verifybox {
    position: relative;
    box-sizing: border-box;
    border-radius: 2px;
    border: 1px solid #e4e7eb;
    background-color: #fff;
    box-shadow: 0 0 10px rgba(0, 0, 0, .3);
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
}

.verifybox-top {
    padding: 0 15px;
    height: 50px;
    line-height: 50px;
    text-align: left;
    font-size: 16px;
    color: #45494c;
    border-bottom: 1px solid #e4e7eb;
    box-sizing: border-box;
}

.verifybox-bottom {
    padding: 15px;
    box-sizing: border-box;
}

.verifybox-close {
    position: absolute;
    top: 13px;
    right: 9px;
    width: 24px;
    height: 24px;
    text-align: center;
    cursor: pointer;
}

.verifybox-close i {
    position: absolute;
    right: 4px;
}

.mask {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 1001;
    width: 100%;
    height: 100vh;
    background: rgba(0, 0, 0, .3);
    transition: all .5s;
}

.verify-tips {
    position: absolute;
    left: 0px;
    bottom: 0px;
    width: 100%;
    height: 30px;
    line-height: 30px;
    color: #fff;
    padding-left: 8px;
}

.suc-bg {
    background-color: rgba(92, 184, 92, .5);
    filter: progid:DXImageTransform.Microsoft.gradient(startcolorstr=#7f5CB85C, endcolorstr=#7f5CB85C);
}

.err-bg {
    background-color: rgba(217, 83, 79, .5);
    filter: progid:DXImageTransform.Microsoft.gradient(startcolorstr=#7fD9534F, endcolorstr=#7fD9534F);
}

.tips-enter, .tips-leave-to {
    bottom: -30px;
}

.tips-enter-active, .tips-leave-active {
    transition: bottom .5s;
}

/* ---------------------------- */
/*常规验证码*/
.verify-code {
    font-size: 20px;
    text-align: center;
    cursor: pointer;
    margin-bottom: 5px;
    border: 1px solid var(--el-border-color);
}

.cerify-code-panel {
    height: 100%;
    overflow: hidden;
}

.verify-code-area {
    float: left;
}

.verify-input-area {
    float: left;
    width: 60%;
    padding-right: 10px;

}

.verify-change-area {
    line-height: 30px;
    float: left;
}

.varify-input-code {
    display: inline-block;
    width: 100%;
    height: 25px;
}

.verify-change-code {
    color: var(--el-color-primary);
    cursor: pointer;
}

.verify-btn {
    width: 200px;
    height: 30px;
    background-color: var(--el-color-primary);
    color: #FFFFFF;
    border: none;
    margin-top: 10px;
}


/*滑动验证码*/
.verify-bar-area {
    position: relative;
    background: #FFFFFF;
    text-align: center;
    -webkit-box-sizing: content-box;
    -moz-box-sizing: content-box;
    box-sizing: content-box;
    border: 1px solid var(--el-border-color);
    -webkit-border-radius: 4px;
    border-radius: 0;
}

.verify-bar-area .verify-move-block {
    position: absolute;
    top: 0px;
    left: 0;
    background: #fff;
    cursor: pointer;
    -webkit-box-sizing: content-box;
    -moz-box-sizing: content-box;
    box-sizing: content-box;
    box-shadow: 0 0 2px #888888;
    -webkit-border-radius: 1px;
}

.verify-bar-area .verify-move-block:hover {
    background-color: var(--el-color-primary);
    color: #FFFFFF;
}

.verify-bar-area .verify-left-bar {
    position: absolute;
    top: -1px;
    left: -1px;
    background: #f0fff0;
    cursor: pointer;
    -webkit-box-sizing: content-box;
    -moz-box-sizing: content-box;
    box-sizing: content-box;
    border: 1px solid var(--el-border-color);
}

.verify-img-panel {
    margin: 0;
    -webkit-box-sizing: content-box;
    -moz-box-sizing: content-box;
    box-sizing: content-box;
    position: relative;
}

.verify-img-panel .verify-refresh {
    width: 24px;
    height: 24px;
    text-align: center;
    padding: 4px;
    cursor: pointer;
    position: absolute;
    top: 0;
    right: 0;
    z-index: 2;
    background-color: rgba(0, 0, 0, 0.2);
}

.verify-img-panel .verify-refresh i {
    color: white;
    position: absolute;
    right: 4px;
}

.verify-img-panel .verify-gap {
    background-color: #fff;
    position: relative;
    z-index: 2;
    border: 1px solid #fff;
}

.verify-bar-area .verify-move-block .verify-sub-block {
    position: absolute;
    text-align: center;
    z-index: 3;
    /* border: 1px solid #fff; */
}

.verify-bar-area .verify-move-block .verify-icon {
    position: absolute;
    top: 11px;
    right: 11px;
}

.verify-bar-area .verify-msg {
    z-index: 3;
}
</style>
