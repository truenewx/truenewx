<template>
    <el-icon :class="className" :color="color" :style="style">
        <Loading v-if="type === 'Loading'"/>
        <ArrowDown v-else-if="type === 'ArrowDown'"/>
        <ArrowLeft v-else-if="type === 'ArrowLeft'"/>
        <ArrowRight v-else-if="type === 'ArrowRight'"/>
        <Bottom v-else-if="type === 'Bottom'"/>
        <CaretBottom v-else-if="type === 'CaretBottom'"/>
        <CaretTop v-else-if="type === 'CaretTop'"/>
        <Check v-else-if="type === 'Check'"/>
        <CircleCheck v-else-if="type === 'CircleCheck'"/>
        <CircleCheckFilled v-else-if="type === 'CircleCheckFilled'"/>
        <CircleClose v-else-if="type === 'CircleClose'"/>
        <CircleCloseFilled v-else-if="type === 'CircleCloseFilled'"/>
        <Close v-else-if="type === 'Close'"/>
        <CloseBold v-else-if="type === 'CloseBold'"/>
        <CopyDocument v-else-if="type === 'CopyDocument'"/>
        <Document v-else-if="type === 'Document'"/>
        <Delete v-else-if="type === 'Delete'"/>
        <DeleteFilled v-else-if="type === 'DeleteFilled'"/>
        <DocumentCopy v-else-if="type === 'DocumentCopy'"/>
        <Edit v-else-if="type === 'Edit'"/>
        <Folder v-else-if="type === 'Folder'"/>
        <HomeFilled v-else-if="type === 'HomeFilled'"/>
        <InfoFilled v-else-if="type === 'InfoFilled'"/>
        <OfficeBuilding v-else-if="type === 'OfficeBuilding'"/>
        <Plus v-else-if="type === 'Plus'"/>
        <Pointer v-else-if="type === 'Pointer'"/>
        <QuestionFilled v-else-if="type === 'QuestionFilled'"/>
        <Rank v-else-if="type === 'Rank'"/>
        <Remove v-else-if="type === 'Remove'"/>
        <RemoveFilled v-else-if="type === 'RemoveFilled'"/>
        <Right v-else-if="type === 'Right'"/>
        <Search v-else-if="type === 'Search'"/>
        <Select v-else-if="type === 'Select'"/>
        <Setting v-else-if="type === 'Setting'"/>
        <Sort v-else-if="type === 'Sort'"/>
        <SuccessFilled v-else-if="type === 'SuccessFilled'"/>
        <Switch v-else-if="type === 'Switch'"/>
        <Top v-else-if="type === 'Top'"/>
        <UserFilled v-else-if="type === 'UserFilled'"/>
        <View v-else-if="type === 'View'"/>
        <WarningFilled v-else-if="type === 'WarningFilled'"/>
        <ZoomIn v-else-if="type === 'ZoomIn'"/>
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
        type: String,
        color: {},
        size: Number,
        width: [Number, String],
        height: [Number, String],
    },
    computed: {
        isUrlType() {
            return this.type && (
                this.type.startsWith('https://') || this.type.startsWith('http://')
                || this.type.startsWith('../') || this.type.startsWith('./') || this.type.startsWith('/')
            );
        },
        className() {
            if (this.isUrlType) {
                return undefined;
            }
            if (this.type && components[this.type]) {
                let className = 'tnxel-icon-' + this.type;
                if (this.type === 'Loading') {
                    className += ' is-loading';
                }
                return className;
            } else if (this.type === 'loading') {
                return 'tnxel-icon-' + this.type + ' el-icon-loading';
            } else {
                return this.type;
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
                style['background-image'] = 'url(' + this.type + ')';
                style['background-size'] = style.width + ' ' + style.height;
            }
            return style;
        }
    },
}
</script>
