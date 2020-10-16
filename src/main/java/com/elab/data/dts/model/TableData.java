package com.elab.data.dts.model;

import com.elab.data.dts.formats.avro.Operation;

/**
 * 表结构
 *
 * @author ： liukx
 * @time ： 2020/9/23 - 14:09
 */
public class TableData {

    /**
     * 数据库名称
     */
    private String databaseName;
    /**
     * 表名称
     */
    private String tableName;

    /**
     * 操作类型
     */
    private Operation operation;

    /**
     * 来源的时间戳
     */
    private Long sourceTimestamp;

    public Long getSourceTimestamp() {
        return sourceTimestamp;
    }

    public void setSourceTimestamp(Long sourceTimestamp) {
        this.sourceTimestamp = sourceTimestamp;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

}
