package com.elab.data.dts.model;

import java.util.List;
import java.util.Map;

/**
 * 增删改操作实体
 *
 * @author ： liukx
 * @time ： 2020/9/27 - 16:00
 */
public class DMLData extends TableData {

    /**
     * 字段数据信息
     */
    private Map<String, FieldData> fieldDataMap;
    /**
     * 变更字段
     */
    private List<String> changeFieldList;

    /**
     * 数据的主键
     */
    private String id;

    public Map<String, FieldData> getFieldDataMap() {
        return fieldDataMap;
    }

    public List<String> getChangeFieldList() {
        return changeFieldList;
    }

    public void setChangeFieldList(List<String> changeFieldList) {
        this.changeFieldList = changeFieldList;
    }

    public void setFieldDataMap(Map<String, FieldData> fieldDataMap) {
        this.fieldDataMap = fieldDataMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
