package com.elab.data.dts.utils;

import com.elab.data.dts.common.FieldEntryHolder;
import com.elab.data.dts.common.Util;
import com.elab.data.dts.formats.avro.Decimal;
import com.elab.data.dts.formats.avro.Field;
import com.elab.data.dts.formats.avro.Record;
import com.elab.data.dts.model.DDLData;
import com.elab.data.dts.model.DMLData;
import com.elab.data.dts.model.FieldData;
import com.elab.data.dts.model.TableData;
import com.elab.data.dts.recordprocessor.FieldConverter;

import java.util.*;

/**
 * 数据解析工具类
 *
 * @author ： liukx
 * @time ： 2020/9/23 - 14:30
 */
public class DataParseUtils {
    private static final FieldConverter FIELD_CONVERTER = FieldConverter.getConverter("mysql", null);


    private static Class[] fieldClass = new Class[256];

    static {
        fieldClass[0] = Decimal.class;

        fieldClass[1] = Integer.class;
        //Type.INT16;
        fieldClass[2] = Integer.class;
        //Type.INT32;
        fieldClass[3] = Integer.class;

        //Type.FLOAT
        fieldClass[4] = Double.class;
        //Type.DOUBLE
        fieldClass[5] = Double.class;

        //Type.NULL
        fieldClass[6] = String.class;

        //Type.TIMESTAMP
        fieldClass[7] = Date.class;

        //Type.INT64
        fieldClass[8] = Integer.class;
        //Type.INT24
        fieldClass[9] = Integer.class;

        //Type.DATE
        fieldClass[10] = Date.class;
        //Type.TIME
        fieldClass[11] = Date.class;
        //Type.DATETIME
        fieldClass[12] = Date.class;
        //Type.YEAR
        fieldClass[13] = Date.class;
        //Type.DATETIME
        fieldClass[14] = Date.class;
        //Type.STRING
        fieldClass[15] = String.class;
        //Type.BIT
        fieldClass[16] = Integer.class;

        fieldClass[255] = String.class;    //Type.GEOMETRY;
        fieldClass[254] = String.class; //Type.STRING;
        fieldClass[253] = String.class; //Type.STRING;

        fieldClass[252] = String.class; //Type.BLOB;
        fieldClass[251] = String.class; //Type.BLOB;
        fieldClass[250] = String.class; //Type.BLOB;
        fieldClass[249] = String.class; //Type.BLOB;

        fieldClass[246] = Long.class; //Type.DECIMAL;

        fieldClass[248] = String.class; //Type.SET;
        fieldClass[247] = String.class; //Type.ENUM;
        fieldClass[245] = String.class;  //Type.JSON;
    }

    /**
     * 解析修改类型
     *
     * @param record
     * @param changeFieldList
     * @return
     */
    public static Map<String, FieldData> parseUpdateField(Record record, List<String> changeFieldList) {
        List<Field> fields = (List<Field>) record.getFields();
        FieldEntryHolder[] fieldArray = getFieldEntryHolder(record);
        FieldEntryHolder before = fieldArray[0];
        FieldEntryHolder after = fieldArray[1];

        Map<String, FieldData> fieldDataMap = new LinkedHashMap<>();
        if (null != fields) {
            Iterator<Field> fieldIterator = fields.iterator();
            while (fieldIterator.hasNext() && before.hasNext() && after.hasNext()) {
                FieldData fieldData = new FieldData();
                Field field = fieldIterator.next();
                fieldData.setField(field.getName());
                Integer dataTypeNumber = field.getDataTypeNumber();

                Class fieldType = getFieldType(dataTypeNumber);
                fieldData.setDataType(fieldType);

                Object toPrintBefore = before.take();
                if (toPrintBefore != null) {
                    Object beforeValue = FIELD_CONVERTER.convert(field, toPrintBefore).toString();
                    fieldData.setOldValue(beforeValue);
                }

                Object toPrintAfter = after.take();
                if (toPrintAfter != null) {
                    Object afterValue = FIELD_CONVERTER.convert(field, toPrintAfter).toString();
                    fieldData.setValue(afterValue);
                }

                if (toPrintAfter != null) {
                    if (toPrintBefore != null) {
                        if (!toPrintAfter.equals(toPrintBefore)) {
                            changeFieldList.add(field.getName());
                        }
                    } else {
                        changeFieldList.add(field.getName());
                    }
                }
                fieldDataMap.put(field.getName(), fieldData);
            }
        }
        return fieldDataMap;
    }


    private static void parseDatabaseInfo(Record record, TableData tableData) {
        String dbName = null;
        String tableName = null;
        // here we get db and table name
        String[] dbPair = Util.uncompressionObjectName(record.getObjectName());
        if (null != dbPair) {
            if (dbPair.length == 2) {
                dbName = dbPair[0];
                tableName = dbPair[1];
            } else if (dbPair.length == 3) {
                dbName = dbPair[0];
                tableName = dbPair[2];
            } else if (dbPair.length == 1) {
                dbName = dbPair[0];
                tableName = "";
            } else {
                throw new RuntimeException("invalid db and table name pair for record [" + record + "]");
            }
            tableData.setDatabaseName(dbName);
            tableData.setTableName(tableName);
        }
    }

    /**
     * 解析增删改语句内容对象
     *
     * @param record
     * @return
     */
    public static DMLData parseDML(Record record) {
        DMLData dmlData = new DMLData();
        parseDatabaseInfo(record, dmlData);
        dmlData.setOperation(record.getOperation());
        List<String> changeFieldList = new ArrayList<>();
        Map<String, FieldData> fieldDataMap = parseUpdateField(record, changeFieldList);
        dmlData.setFieldDataMap(fieldDataMap);
        dmlData.setChangeFieldList(changeFieldList);
        dmlData.setSourceTimestamp(record.getSourceTimestamp());
        return dmlData;
    }

    /**
     * 解析DDL语句对象
     *
     * @param record
     * @return
     */
    public static DDLData parseDDL(Record record) {
        DDLData ddlData = new DDLData();
        parseDatabaseInfo(record, ddlData);
        ddlData.setSql(record.getAfterImages().toString());
        ddlData.setOperation(record.getOperation());
        ddlData.setSourceTimestamp(record.getSourceTimestamp());
        return ddlData;
    }


    private static FieldEntryHolder[] getFieldEntryHolder(Record record) {
        // this is a simple impl, may exist unhandled situation
        FieldEntryHolder[] fieldArray = new FieldEntryHolder[2];

        fieldArray[0] = new FieldEntryHolder((List<Object>) record.getBeforeImages());
        fieldArray[1] = new FieldEntryHolder((List<Object>) record.getAfterImages());

        return fieldArray;
    }

    /**
     * 获取字段的类型
     *
     * @param typeNumber
     * @return
     */
    public static Class getFieldType(int typeNumber) {
        return fieldClass[typeNumber];
    }

}
