package com.elab.data.dts.model;

/**
 * 字段属性
 *
 * @author ： liukx
 * @time ： 2020/9/23 - 14:06
 */
public class FieldData {

    /**
     * 字段名称
     */
    private String field;
    /**
     * 当前字段值
     */
    private Object value;
    /**
     * 字段类型
     */
    private Class dataType;
    /**
     * 老的值
     */
    private Object oldValue;

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Class getDataType() {
        return dataType;
    }

    public void setDataType(Class dataType) {
        this.dataType = dataType;
    }
}
