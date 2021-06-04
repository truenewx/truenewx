package org.truenewx.tnxjeex.office.excel.imports;

import java.util.ArrayList;
import java.util.List;

import org.truenewx.tnxjee.service.exception.model.CodedError;

/**
 * Excel导入时的工作表模型
 *
 * @author jianglei
 */
public abstract class ImportingExcelSheetModel<T extends ImportingExcelRowModel> {

    private String name;
    private List<T> records;
    private List<CodedError> errors = new ArrayList<>();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<T> getRecords() {
        return this.records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public List<CodedError> getErrors() {
        return this.errors;
    }

}
