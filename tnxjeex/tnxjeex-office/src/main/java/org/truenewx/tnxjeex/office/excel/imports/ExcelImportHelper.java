package org.truenewx.tnxjeex.office.excel.imports;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.enums.EnumItem;
import org.truenewx.tnxjee.core.enums.EnumItemKey;
import org.truenewx.tnxjee.core.enums.EnumType;
import org.truenewx.tnxjee.core.spec.BooleanEnum;
import org.truenewx.tnxjee.core.spec.EnumGrouped;
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
import org.truenewx.tnxjeex.office.excel.ExcelExceptionCodes;
import org.truenewx.tnxjeex.office.excel.ExcelRow;

/**
 * Excel导入协助者
 *
 * @author jianglei
 */
@Component
public class ExcelImportHelper {

    private static final String GROUPED_ENUM_CAPTION_SEPARATOR = "constant.tnxjeex.office.excel.grouped_enum_caption_separator";

    @Autowired
    private CodedErrorResolver codedErrorResolver;
    @Autowired
    private EnumDictResolver enumDictResolver;
    @Autowired
    private RegionSource regionSource;
    @Autowired
    private MessageSource messageSource;

    public String getGroupedCaptionSeparator(Locale locale) {
        return this.messageSource.getMessage(GROUPED_ENUM_CAPTION_SEPARATOR, null, locale);
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

    public void addCellError(ImportingExcelRowModel rowModel, String fieldName, Object fieldValue, BusinessException be,
            Locale locale) {
        addCellError(rowModel, fieldName, fieldValue, be.getCode(), locale, be.getArgs());
    }

    public void addCellRequiredError(ImportingExcelRowModel rowModel, String fieldName, Locale locale) {
        addCellError(rowModel, fieldName, null, ExcelExceptionCodes.IMPORT_CELL_REQUIRED, locale);
    }

    public void addCellRequiredError(ImportingExcelRowModel rowModel, String fieldName, int index, Locale locale) {
        addCellError(rowModel, fieldName, index, null, ExcelExceptionCodes.IMPORT_CELL_REQUIRED, locale);
    }

    public void applyValue(ImportingExcelRowModel rowModel, ExcelRow row, int columnIndex, String fieldName,
            Locale locale, boolean required) {
        Object value = getCellValue(rowModel, row, columnIndex, fieldName, locale);
        applyValue(rowModel, value, fieldName, locale, required);
    }

    @SuppressWarnings("unchecked")
    public <V> V getCellValue(ImportingExcelRowModel rowModel, ExcelRow row, int columnIndex, String fieldName,
            Locale locale) {
        Field field = ClassUtil.findField(rowModel.getClass(), fieldName);
        Class<?> fieldType = field.getType();
        if (fieldType == LocalDate.class) {
            return (V) row.getLocalDateCellValue(columnIndex);
        } else if (ClassUtil.isNumeric(fieldType)) {
            BigDecimal decimal = row.getNumericCellValue(columnIndex);
            return (V) MathUtil.toValue(decimal, fieldType);
        } else { // 其它都从字符串解析
            String text = row.getStringCellValue(columnIndex);
            if (fieldType.isArray()) {
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
            String value = text;
            EnumItemKey enumItemKey = field.getAnnotation(EnumItemKey.class);
            if (enumItemKey != null) {
                EnumType enumType = this.enumDictResolver.getEnumType(enumItemKey.type(), enumItemKey.subtype(),
                        locale);
                if (enumType != null) {
                    EnumItem enumItem = enumType.getItemByCaption(value);
                    if (enumItem != null) {
                        return (V) enumItem.getKey();
                    }
                }
                if (componentIndex == null) {
                    addCellError(rowModel, fieldName, value, ExcelExceptionCodes.IMPORT_CELL_ENUM_ERROR, locale);
                } else {
                    addCellError(rowModel, fieldName, componentIndex, value, ExcelExceptionCodes.IMPORT_CELL_ENUM_ERROR,
                            locale);
                }
            }
            RegionCode regionCode = field.getAnnotation(RegionCode.class);
            if (regionCode != null) {
                Region region = getNationalRegionSource().parseSubRegion(value, regionCode.withSuffix(), locale);
                if (region != null) {
                    return (V) region.getCode();
                }
                if (componentIndex == null) {
                    addCellError(rowModel, fieldName, value, ExcelExceptionCodes.IMPORT_CELL_REGION_ERROR, locale);
                } else {
                    addCellError(rowModel, fieldName, componentIndex, value,
                            ExcelExceptionCodes.IMPORT_CELL_REGION_ERROR, locale);
                }
            }
            return (V) value;
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

    public void applyValue(ImportingExcelRowModel rowModel, Object fieldValue, String fieldName, Locale locale,
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

}
