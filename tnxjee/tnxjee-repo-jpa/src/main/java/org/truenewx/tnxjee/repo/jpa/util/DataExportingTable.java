package org.truenewx.tnxjee.repo.jpa.util;

import java.util.List;

/**
 * 数据导出时的数据库表
 *
 * @author jianglei
 */
public class DataExportingTable {

    private String tableName; // 表名
    private List<String> columnNames; // 列名清单，顺序敏感
    private List<Object[]> records; // 数据记录集
    private boolean morePage; // 是否还有更多页数据

    public DataExportingTable(String tableName, List<String> columnNames) {
        this.tableName = tableName;
        this.columnNames = columnNames;
    }

    public String getTableName() {
        return this.tableName;
    }

    public List<String> getColumnNames() {
        return this.columnNames;
    }

    public List<Object[]> getRecords() {
        return this.records;
    }

    public void setRecords(List<Object[]> records) {
        this.records = records;
    }

    public boolean isMorePage() {
        return this.morePage;
    }

    public void setMorePage(boolean morePage) {
        this.morePage = morePage;
    }

}
