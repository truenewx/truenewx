<template>
    <el-icon :class="className" :color="color" :style="style">
        <Loading v-if="type === 'Loading'"/>
        <ArrowDown v-else-if="type === 'ArrowDown'"/>
        <ArrowRight v-else-if="type === 'ArrowRight'"/>
        <Bottom v-else-if="type === 'Bottom'"/>
        <CaretTop v-else-if="type === 'CaretTop'"/>
        <CircleCheckFilled v-else-if="type === 'CircleCheckFilled'"/>
        <CircleClose v-else-if="type === 'CircleClose'"/>
        <CircleCloseFilled v-else-if="type === 'CircleCloseFilled'"/>
        <Close v-else-if="type === 'Close'"/>
        <CloseBold v-else-if="type === 'CloseBold'"/>
        <CopyDocument v-else-if="type === 'CopyDocument'"/>
        <DeleteFilled v-else-if="type === 'DeleteFilled'"/>
        <DocumentCopy v-else-if="type === 'DocumentCopy'"/>
        <Edit v-else-if="type === 'Edit'"/>
        <Folder v-else-if="type === 'Folder'"/>
        <HomeFilled v-else-if="type === 'HomeFilled'"/>
        <InfoFilled v-else-if="type === 'InfoFilled'"/>
        <OfficeBuilding v-else-if="type === 'OfficeBuilding'"/>
        <Plus v-else-if="type === 'Plus'"/>
        <QuestionFilled v-else-if="type === 'QuestionFilled'"/>
        <Right v-else-if="type === 'Right'"/>
        <Search v-else-if="type === 'Search'"/>
        <Select v-else-if="type === 'Select'"/>
        <Setting v-else-if="type === 'Setting'"/>
        <Top v-else-if="type === 'Top'"/>
        <UserFilled v-else-if="type === 'UserFilled'"/>
        <View v-else-if="type === 'View'"/>
        <WarningFilled v-else-if="type === 'WarningFilled'"/>
    </el-icon>
</template>

<script>
import {
    ArrowDown,
    ArrowRight,
    Bottom,
    CaretTop,
    CircleCheckFilled,
    CircleClose,
    CircleCloseFilled,
    Close,
    CloseBold,
    CopyDocument,
    DeleteFilled,
    DocumentCopy,
    Edit,
    Folder,
    HomeFilled,
    InfoFilled,
    Loading,
    OfficeBuilding,
    Plus,
    QuestionFilled,
    Right,
    Search,
    Select,
    Setting,
    SuccessFilled,
    Top,
    UserFilled,
    View,
    WarningFilled
} from '@element-plus/icons';

const components = {
    ArrowDown,
    ArrowRight,
    Bottom,
    CaretTop,
    CircleCheckFilled,
    CircleClose,
    CircleCloseFilled,
    Close,
    CloseBold,
    CopyDocument,
    DeleteFilled,
    DocumentCopy,
    Edit,
    Folder,
    HomeFilled,
    InfoFilled,
    Loading,
    OfficeBuilding,
    Plus,
    QuestionFilled,
    Right,
    Search,
    Select,
    Setting,
    SuccessFilled,
    Top,
    UserFilled,
    View,
    WarningFilled,
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
            if (this.width) {
                style.width = this.width;
                if (typeof this.width === 'number') {
                    style.width += 'px';
                }
            }
            if (this.height) {
                style.height = this.height;
                if (typeof this.height === 'number') {
                    style.height += 'px';
                }
            }
            return style;
        }
    }
}
</script>
