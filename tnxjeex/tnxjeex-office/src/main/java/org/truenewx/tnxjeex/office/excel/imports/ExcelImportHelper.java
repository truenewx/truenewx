package org.truenewx.tnxjeex.office.excel.imports;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.enums.EnumItem;
import org.truenewx.tnxjee.core.enums.EnumType;
import org.truenewx.tnxjee.core.enums.annotation.EnumItemKey;
import org.truenewx.tnxjee.core.message.MessageResolver;
import org.truenewx.tnxjee.core.spec.BooleanEnum;
import org.truenewx.tnxjee.core.spec.EnumGrouped;
import org.truenewx.tnxjee.core.spec.PermanentableDate;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.model.validation.constraint.RegionCode;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.exception.message.CodedErrorResolver;
import org.truenewx.tnxjee.service.exception.model.CodedError;
import org.truenewx.tnxjee.service.spec.region.NationalRegionSource;
import org.truenewx.tnxjee.service.spec.region.Region;
import org.truenewx.tnxjee.service.spec.region.RegionNationCodes;
import org.truenewx.tnxjee.service.spec.region.RegionSource;
import org.truenewx.tnxjeex.office.excel.ExcelCellFormatException;
import org.truenewx.tnxjeex.office.excel.ExcelExceptionCodes;
import org.truenewx.tnxjeex.office.excel.ExcelRow;

/**
 * Excel导入协助者
 *
 * @author jianglei
 */
@Component
public class ExcelImportHelper {

    private static final String CONSTANT_GROUPED_ENUM_CAPTION_SEPARATOR = "constant.tnxjeex.office.excel.grouped_enum_caption_separator";
    private static final String CONSTANT_PERMANENT_DATE_DEFAULT = "constant.tnxjeex.office.excel.permanent_date_default";
    private static final String CONSTANT_PERMANENT_DATE = "constant.tnxjeex.office.excel.permanent_date";

    @Autowired
    private CodedErrorResolver codedErrorResolver;
    @Autowired
    private EnumDictResolver enumDictResolver;
    @Autowired
    private RegionSource regionSource;
    @Autowired
    private MessageResolver messageResolver;

    public String getGroupedCaptionSeparator(Locale locale) {
        return this.messageResolver.resolveMessage(CONSTANT_GROUPED_ENUM_CAPTION_SEPARATOR, locale);
    }

    private String getPermanentDateText(Locale locale) {
        String text = this.messageResolver.resolveMessage(CONSTANT_PERMANENT_DATE, locale);
        if (CONSTANT_PERMANENT_DATE.equals(text)) {
            text = this.messageResolver.resolveMessage(CONSTANT_PERMANENT_DATE_DEFAULT, locale);
        }
        return text;
    }

    public void addSheetError(ImportingExcelSheetModel<?> sheetModel, String code, Locale locale, Object... args) {
        CodedError error = this.codedErrorResolver.resolveError(code, locale, args);
        sheetModel.getErrors().add(error);
    }

    public void addRowError(ImportingExcelRowModel rowModel, String code, Locale locale, Object... args) {
        CodedError error = this.codedErrorResolver.resolveError(code, locale, args);
        rowModel.getRowErrors().add(error);
    }

    public void addCellError(ImportingExcelRowModel rowModel, String fieldName, Object fieldValue, String errorCode,
            Locale locale, Object... args) {
        if (errorCode.startsWith(Strings.LEFT_BRACE) && errorCode.endsWith(Strings.RIGHT_BRACE)) {
            errorCode = errorCode.substring(1, errorCode.length() - 1);
        }
        CodedError error = this.codedErrorResolver.resolveError(errorCode, locale, args);
        rowModel.addFieldError(fieldName, fieldValue == null ? null : fieldValue.toString(), error);
    }

    public void addCellError(ImportingExcelRowModel rowModel, String fieldName, int index, Object fieldValue,
            String errorCode, Locale locale, Object... args) {
        CodedError error = this.codedErrorResolver.resolveError(errorCode, locale, args);
        rowModel.addFieldError(fieldName, index, fieldValue == null ? null : fieldValue.toString(), error);
    }

    public void addCellBusinessError(ImportingExcelRowModel rowModel, String fieldName, Object fieldValue,
            BusinessException be, Locale locale) {
        addCellError(rowModel, fieldName, fieldValue, be.getCode(), locale, be.getArgs());
    }

    public void addCellBusinessError(ImportingExcelRowModel rowModel, String fieldName, int index, Object fieldValue,
            BusinessException be, Locale locale) {
        addCellError(rowModel, fieldName, index, fieldValue, be.getCode(), locale, be.getArgs());
    }

    public void addCellFormatError(ImportingExcelRowModel rowModel, String fieldName, ExcelCellFormatException fe,
            Locale locale) {
        fe.clearAddress(); // 作为字段错误处理时清除单元格地址信息
        addCellError(rowModel, fieldName, fe.getOriginalText(), fe.getCode(), locale, fe.getArgs());
    }

    public void addCellFormatError(ImportingExcelRowModel rowModel, String fieldName, int index,
            ExcelCellFormatException fe, Locale locale) {
        fe.clearAddress(); // 作为字段错误处理时清除单元格地址信息
        addCellError(rowModel, fieldName, index, fe.getOriginalText(), fe.getCode(), locale, fe.getArgs());
    }

    public void addCellRequiredError(ImportingExcelRowModel rowModel, String fieldName, Locale locale) {
        if (!rowModel.containsFieldError(fieldName, null)) { // 字段没有其它错误才可添加必填错误
            addCellError(rowModel, fieldName, null, ExcelExceptionCodes.IMPORT_CELL_REQUIRED, locale);
        }
    }

    public void addCellRequiredError(ImportingExcelRowModel rowModel, String fieldName, int index, Locale locale) {
        if (!rowModel.containsFieldError(fieldName, index)) { // 字段没有其它错误才可添加必填错误
            addCellError(rowModel, fieldName, index, null, ExcelExceptionCodes.IMPORT_CELL_REQUIRED, locale);
        }
    }

    public void applyValue(ImportingExcelRowModel rowModel, ExcelRow row, int columnIndex, String fieldName,
            Locale locale, boolean required) {
        Object value = getCellValue(rowModel, row, columnIndex, fieldName, locale);
        applyValue(rowModel, fieldName, value, locale, required);
    }

    /**
     * 获取指定行指定列的单元格的值，如果出现读取错误，则将错误消息加入行数据模型中的字段错误集中
     */
    @SuppressWarnings("unchecked")
    public <V> V getCellValue(ImportingExcelRowModel rowModel, ExcelRow row, int columnIndex, String fieldName,
            Locale locale) {
        try {
            Field field = ClassUtil.findField(rowModel.getClass(), fieldName);
            Class<?> fieldType = field.getType();
            if (ClassUtil.isNumeric(fieldType)) {
                BigDecimal decimal = row.getNumericCellValue(columnIndex);
                return (V) MathUtil.toValue(decimal, fieldType);
            } else if (fieldType == LocalDate.class) {
                return (V) row.getLocalDateCellValue(columnIndex);
            } else if (fieldType == PermanentableDate.class) {
                return (V) getPermanentableDateCellValue(row, columnIndex, locale);
            } else { // 其它都从字符串解析
                String text = row.getStringCellValue(columnIndex);
                if (fieldType.isArray()) { // 为便于获取元素类型，行数据模型中的属性仅支持数组而不支持集合
                    if (StringUtils.isBlank(text)) {
                        return null;
                    }
                    Class<?> componentType = fieldType.getComponentType();
                    String[] texts = text.split("\n");
                    Object array = Array.newInstance(componentType, texts.length);
                    for (int i = 0; i < texts.length; i++) {
                        Object value = parseFromString(rowModel, field, i, texts[i], locale);
                        Array.set(array, i, value);
                    }
                    return (V) array;
                } else {
                    return parseFromString(rowModel, field, null, text, locale);
                }
            }
        } catch (ExcelCellFormatException fe) {
            addCellFormatError(rowModel, fieldName, fe, locale);
        }
        return null;
    }

    public PermanentableDate getPermanentableDateCellValue(ExcelRow row, int columnIndex, Locale locale) {
        return row.getPermanentableDateCellValue(columnIndex, () -> getPermanentDateText(locale));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <V> V parseFromString(ImportingExcelRowModel rowModel, Field field, Integer componentIndex, String text,
            Locale locale) {
        Class<?> fieldType = field.getType();
        if (componentIndex != null) {
            fieldType = fieldType.getComponentType();
        }
        String fieldName = field.getName();
        if (fieldType == String.class) {
            EnumItemKey enumItemKey = field.getAnnotation(EnumItemKey.class);
            if (enumItemKey != null) {
                EnumType enumType = this.enumDictResolver.getEnumType(enumItemKey.type(), enumItemKey.subtype(),
                        locale);
                if (enumType != null) {
                    EnumItem enumItem = enumType.getItemByCaption(text);
                    if (enumItem != null) {
                        return (V) enumItem.getKey();
                    }
                }
                if (componentIndex == null) {
                    addCellError(rowModel, fieldName, text, ExcelExceptionCodes.IMPORT_CELL_ENUM_ERROR, locale);
                } else {
                    addCellError(rowModel, fieldName, componentIndex, text, ExcelExceptionCodes.IMPORT_CELL_ENUM_ERROR,
                            locale);
                }
            }
            RegionCode regionCode = field.getAnnotation(RegionCode.class);
            if (regionCode != null) {
                Region region = getNationalRegionSource().parseSubRegion(text, regionCode.withSuffix(), locale);
                if (region != null) {
                    return (V) region.getCode();
                }
                if (componentIndex == null) {
                    addCellError(rowModel, fieldName, text, ExcelExceptionCodes.IMPORT_CELL_REGION_ERROR, locale);
                } else {
                    addCellError(rowModel, fieldName, componentIndex, text,
                            ExcelExceptionCodes.IMPORT_CELL_REGION_ERROR, locale);
                }
            }
            return (V) text;
        } else if (fieldType.isEnum()) {
            if (StringUtils.isNotBlank(text)) {
                Class<Enum> enumClass = (Class<Enum>) fieldType;
                return (V) getEnumValue(rowModel, fieldName, componentIndex, text, enumClass, locale);
            }
            return null;
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            if (StringUtils.isNotBlank(text)) {
                BooleanEnum enumValue = getEnumValue(rowModel, fieldName, componentIndex, text, BooleanEnum.class,
                        locale);
                return (V) Boolean.valueOf(enumValue == BooleanEnum.TRUE);
            }
            return null;
        }
        throw new BusinessException(ExcelExceptionCodes.IMPORT_SUPPORTED_FIELD_TYPE,
                rowModel.getClass().getSimpleName(), fieldType.getSimpleName(), fieldName);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <V extends Enum> V getEnumValue(ImportingExcelRowModel rowModel, String fieldName, Integer componentIndex,
            String fieldText, Class<V> enumClass, Locale locale) {
        String caption = fieldText;
        String groupCaption = null;
        if (EnumGrouped.class.isAssignableFrom(enumClass)) {
            String separator = getGroupedCaptionSeparator(locale);
            int index = caption.indexOf(separator);
            if (index > 0) {
                groupCaption = caption.substring(0, index);
                caption = caption.substring(index + separator.length());
            }
        }
        V value = (V) this.enumDictResolver.getEnumConstantByCaption(enumClass, caption, groupCaption, locale);
        if (value == null) {
            if (componentIndex == null) {
                addCellError(rowModel, fieldName, fieldText, ExcelExceptionCodes.IMPORT_CELL_ENUM_ERROR, locale);
            } else {
                addCellError(rowModel, fieldName, componentIndex, fieldText, ExcelExceptionCodes.IMPORT_CELL_ENUM_ERROR,
                        locale);
            }
        }
        return value;
    }

    public NationalRegionSource getNationalRegionSource() {
        return this.regionSource.getNationalRegionSource(RegionNationCodes.CHINA);
    }

    public void applyValue(ImportingExcelRowModel rowModel, String fieldName, Object fieldValue, Locale locale,
            boolean required) {
        if (required) {
            if (fieldValue == null || (fieldValue instanceof String && StringUtils.isBlank((String) fieldValue))) {
                // 要求必填但为空的字段，如果存在其它类型的错误，则不再添加必填错误
                if (!rowModel.getFieldWrongs().containsKey(fieldName)) {
                    addCellRequiredError(rowModel, fieldName, locale);
                    return;
                }
            }
        }
        BeanUtil.setPropertyValue(rowModel, fieldName, fieldValue);
    }

    public void applyLocalMonthValue(ImportingExcelRowModel rowModel, ExcelRow row, int columnIndex, String fieldName,
            Locale locale, boolean required) {
        try {
            LocalDate value = row.getLocalMonthCellValue(columnIndex);
            applyValue(rowModel, fieldName, value, locale, required);
        } catch (ExcelCellFormatException fe) {
            addCellFormatError(rowModel, fieldName, fe, locale);
        }
    }

    public void applyLocalMonthValue(ImportingExcelRowModel rowModel, ExcelRow row, int columnIndex, String fieldName,
            int index, Locale locale, boolean required) {
        try {
            LocalDate value = row.getLocalMonthCellValue(columnIndex);
            applyValue(rowModel, fieldName, value, locale, required);
        } catch (ExcelCellFormatException fe) {
            addCellFormatError(rowModel, fieldName, index, fe, locale);
        }
    }

}
