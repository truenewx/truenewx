<template>
    <el-icon :class="className" :color="color" :style="style">
        <Loading v-if="value === 'Loading'"/>
        <ArrowDown v-else-if="value === 'ArrowDown'"/>
        <ArrowLeft v-else-if="value === 'ArrowLeft'"/>
        <ArrowRight v-else-if="value === 'ArrowRight'"/>
        <Bottom v-else-if="value === 'Bottom'"/>
        <CaretBottom v-else-if="value === 'CaretBottom'"/>
        <CaretTop v-else-if="value === 'CaretTop'"/>
        <Check v-else-if="value === 'Check'"/>
        <CircleCheck v-else-if="value === 'CircleCheck'"/>
        <CircleCheckFilled v-else-if="value === 'CircleCheckFilled'"/>
        <CircleClose v-else-if="value === 'CircleClose'"/>
        <CircleCloseFilled v-else-if="value === 'CircleCloseFilled'"/>
        <Close v-else-if="value === 'Close'"/>
        <CloseBold v-else-if="value === 'CloseBold'"/>
        <CopyDocument v-else-if="value === 'CopyDocument'"/>
        <Document v-else-if="value === 'Document'"/>
        <Delete v-else-if="value === 'Delete'"/>
        <DeleteFilled v-else-if="value === 'DeleteFilled'"/>
        <DocumentCopy v-else-if="value === 'DocumentCopy'"/>
        <Edit v-else-if="value === 'Edit'"/>
        <Folder v-else-if="value === 'Folder'"/>
        <HomeFilled v-else-if="value === 'HomeFilled'"/>
        <InfoFilled v-else-if="value === 'InfoFilled'"/>
        <OfficeBuilding v-else-if="value === 'OfficeBuilding'"/>
        <Plus v-else-if="value === 'Plus'"/>
        <Pointer v-else-if="value === 'Pointer'"/>
        <QuestionFilled v-else-if="value === 'QuestionFilled'"/>
        <Rank v-else-if="value === 'Rank'"/>
        <Remove v-else-if="value === 'Remove'"/>
        <RemoveFilled v-else-if="value === 'RemoveFilled'"/>
        <Right v-else-if="value === 'Right'"/>
        <Search v-else-if="value === 'Search'"/>
        <Select v-else-if="value === 'Select'"/>
        <Setting v-else-if="value === 'Setting'"/>
        <Sort v-else-if="value === 'Sort'"/>
        <SuccessFilled v-else-if="value === 'SuccessFilled'"/>
        <Switch v-else-if="value === 'Switch'"/>
        <Top v-else-if="value === 'Top'"/>
        <UserFilled v-else-if="value === 'UserFilled'"/>
        <View v-else-if="value === 'View'"/>
        <WarningFilled v-else-if="value === 'WarningFilled'"/>
        <ZoomIn v-else-if="value === 'ZoomIn'"/>
    </el-icon>
</template>

<script>
import {
    ArrowDown,
    ArrowLeft,
    ArrowRight,
    Bottom,
    CaretBottom,
    CaretTop,
    Check,
    CircleCheck,
    CircleCheckFilled,
    CircleClose,
    CircleCloseFilled,
    Close,
    CloseBold,
    CopyDocument,
    Delete,
    DeleteFilled,
    Document,
    DocumentCopy,
    Edit,
    Folder,
    HomeFilled,
    InfoFilled,
    Loading,
    OfficeBuilding,
    Plus,
    Pointer,
    QuestionFilled,
    Rank,
    Remove,
    RemoveFilled,
    Right,
    Search,
    Select,
    Setting,
    Sort,
    SuccessFilled,
    Switch,
    Top,
    UserFilled,
    View,
    WarningFilled,
    ZoomIn,
} from '@element-plus/icons-vue';

const components = {
    ArrowDown,
    ArrowLeft,
    ArrowRight,
    Bottom,
    CaretBottom,
    CaretTop,
    Check,
    CircleCheck,
    CircleCheckFilled,
    CircleClose,
    CircleCloseFilled,
    Close,
    CloseBold,
    CopyDocument,
    Document,
    Delete,
    DeleteFilled,
    DocumentCopy,
    Edit,
    Folder,
    HomeFilled,
    InfoFilled,
    Loading,
    OfficeBuilding,
    Plus,
    Pointer,
    QuestionFilled,
    Rank,
    Remove,
    RemoveFilled,
    Right,
    Search,
    Select,
    Setting,
    Sort,
    SuccessFilled,
    Switch,
    Top,
    UserFilled,
    View,
    WarningFilled,
    ZoomIn,
};

export default {
    name: 'TnxelIcon',
    components: components,
    props: {
        value: String,
        color: {},
        size: Number,
        width: [Number, String],
        height: [Number, String],
    },
    computed: {
        isUrlType() {
            return this.value && (
                this.value.startsWith('https://') || this.value.startsWith('http://')
                || this.value.startsWith('../') || this.value.startsWith('./') || this.value.startsWith('/')
            );
        },
        className() {
            if (this.isUrlType) {
                return undefined;
            }
            if (this.value && components[this.value]) {
                let className = 'tnxel-icon-' + this.value;
                if (this.value === 'Loading') {
                    className += ' is-loading';
                }
                return className;
            } else if (this.value === 'loading') {
                return 'tnxel-icon-' + this.value + ' el-icon-loading';
            } else {
                return this.value;
            }
        },
        style() {
            let style = {};
            if (this.size) {
                style['font-size'] = this.size + 'px';
            }
            let width = this.width || this.size;
            if (width) {
                style.width = width;
                if (typeof style.width === 'number') {
                    style.width += 'px';
                }
            }
            let height = this.height || this.size;
            if (height) {
                style.height = height;
                if (typeof style.height === 'number') {
                    style.height += 'px';
                }
            }
            if (this.isUrlType) {
                style['background-repeat'] = 'no-repeat';
                style['background-image'] = 'url(' + this.value + ')';
                style['background-size'] = style.width + ' ' + style.height;
            }
            return style;
        }
    },
}
</script>
