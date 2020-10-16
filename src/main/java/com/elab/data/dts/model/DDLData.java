package com.elab.data.dts.model;

/**
 * 表结构变更
 *
 * @author ： liukx
 * @time ： 2020/9/27 - 15:44
 */
public class DDLData extends TableData {

    private String sql;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
