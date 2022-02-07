<template>
    <el-icon :class="className" :color="color" :style="style">
        <Loading v-if="type === 'Loading'"/>
        <ArrowDown v-else-if="type === 'ArrowDown'"/>
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
        <Right v-else-if="type === 'Right'"/>
        <Search v-else-if="type === 'Search'"/>
        <Select v-else-if="type === 'Select'"/>
        <Setting v-else-if="type === 'Setting'"/>
        <Sort v-else-if="type === 'Sort'"/>
        <SuccessFilled v-else-if="type === 'SuccessFilled'"/>
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
    Right,
    Search,
    Select,
    Setting,
    Sort,
    SuccessFilled,
    Top,
    UserFilled,
    View,
    WarningFilled,
    ZoomIn,
} from '@element-plus/icons-vue';

const components = {
    ArrowDown,
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
    Right,
    Search,
    Select,
    Setting,
    Sort,
    SuccessFilled,
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
        className() {
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
            return style;
        }
    }
}
</script>
