package org.truenewx.tnxjeex.doc.excel.imports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.service.exception.model.CodedError;
import org.truenewx.tnxjee.service.exception.model.TextWrong;

/**
 * Excel导入时的行数据模型
 *
 * @author jianglei
 */
public abstract class ImportingExcelRowModel {

    private List<CodedError> rowErrors = new ArrayList<>();
    private Map<String, TextWrong> fieldWrongs = new HashMap<>();

    public List<CodedError> getRowErrors() {
        return this.rowErrors;
    }

    public Map<String, TextWrong> getFieldWrongs() {
        return this.fieldWrongs;
    }

    public void addFieldError(String fieldName, String originalText, CodedError error) {
        String errorKey = getFieldErrorKey(fieldName, null);
        TextWrong text = this.fieldWrongs.computeIfAbsent(errorKey, k -> new TextWrong(originalText));
        text.getErrors().add(error);
    }

    /**
     * 当字段类型为数组或集合时，添加字段指定位置元素的错误文本
     *
     * @param fieldName    字段名称
     * @param index        出错的元素位置索引
     * @param originalText 原始文本
     * @param error        错误对象
     */
    public void addFieldError(String fieldName, int index, String originalText, CodedError error) {
        String errorKey = getFieldErrorKey(fieldName, index);
        addFieldError(errorKey, originalText, error);
    }

    private String getFieldErrorKey(String fieldName, Integer index) {
        if (index == null) {
            return fieldName;
        } else {
            return fieldName + Strings.LEFT_SQUARE_BRACKET + index + Strings.RIGHT_SQUARE_BRACKET;
        }
    }

    public boolean containsFieldError(String fieldName, Integer index) {
        String errorKey = getFieldErrorKey(fieldName, index);
        return this.fieldWrongs.containsKey(errorKey);
    }

    public TextWrong getFieldWrong(String fieldName, Integer index) {
        String errorKey = getFieldErrorKey(fieldName, index);
        return this.fieldWrongs.get(errorKey);
    }

    public TextWrong removeFieldWrong(String fieldName, Integer index) {
        String errorKey = getFieldErrorKey(fieldName, index);
        return this.fieldWrongs.remove(errorKey);
    }

}
