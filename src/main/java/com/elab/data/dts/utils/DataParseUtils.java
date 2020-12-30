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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 数据解析工具类
 *
 * @author ： liukx
 * @time ： 2020/9/23 - 14:30
 */
public class DataParseUtils {
    private static final FieldConverter FIELD_CONVERTER = FieldConverter.getConverter("mysql", null);
    private static Logger LOG = LoggerFactory.getLogger(DataParseUtils.class);
    public static final FastDateFormat ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT
            = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
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
                    Object beforeValue = getTypeValue(field, toPrintBefore);
                    fieldData.setOldValue(beforeValue);
                }

                Object toPrintAfter = after.take();
                if (toPrintAfter != null) {
                    Object afterValue = getTypeValue(field, toPrintAfter);
                    fieldData.setValue(afterValue);
                }

                if (fieldData.getValue() != null) {
                    if (fieldData.getOldValue() != null) {
                        if (!fieldData.getValue().equals(fieldData.getOldValue())) {
                            changeFieldList.add(field.getName());
                        }
                    } else {
                        changeFieldList.add(field.getName());
                    }
                } else if (fieldData.getOldValue() != null) {
                    if (fieldData.getValue() != null) {
                        if (!fieldData.getValue().equals(fieldData.getOldValue())) {
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

    private static Object getTypeValue(Field field, Object toPrintBefore) {
        Object text = FIELD_CONVERTER.convert(field, toPrintBefore).toString();

        if (text != null && field.getDataTypeNumber() == 7) {
            text = ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(Long.valueOf(text.toString()) * 1000);
        } else {
            text.toString();
        }

        return text;
    }


    public static void parseDatabaseInfo(Record record, TableData tableData) {
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
        dmlData.setId(getFieldValue(fieldDataMap, "id"));
        dmlData.setFieldDataMap(fieldDataMap);
        dmlData.setChangeFieldList(changeFieldList);
        dmlData.setSourceTimestamp(record.getSourceTimestamp());
        return dmlData;
    }

    /**
     * 获取数据的值
     *
     * @param fieldDataMap 数据对象
     * @param fieldName    数据字段名称
     * @return
     */
    private static String getFieldValue(Map<String, FieldData> fieldDataMap, String fieldName) {
        FieldData fieldData = fieldDataMap.get(fieldName);
        if (fieldData != null) {
            Object value = fieldData.getValue();
            if (value != null) {
                return value.toString();
            }

            Object oldValue = fieldData.getOldValue();
            if (value != null) {
                return oldValue.toString();
            }
        }
        return null;
    }

    /**
     * 解析DDL语句对象
     *
     * @param record
     * @return
     */
    public static DDLData parseDDL(Record record) {
        String ddlSQL = record.getAfterImages().toString();
        DDLData ddlData = new DDLData();
        parseDatabaseInfo(record, ddlData);
        ddlData.setSql(ddlSQL);
        ddlData.setOperation(record.getOperation());
        ddlData.setTableName(getDDLTableName(ddlSQL));
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

    /**
     * 获取DDL中的表名
     * // 莫名其妙的SQL语句太多了,诶~~~
     *
     * @param ddlSql
     * @return
     */
    public static String getDDLTableName(String ddlSql) {
        String tableName = null;
        try {
            if (StringUtils.isEmpty(ddlSql)) {
                return null;
            }

            String ddlSqlLowerCase = ddlSql.toLowerCase();

            if (ddlSqlLowerCase.indexOf("add index") > -1) {
                tableName = getTableNameBySql(ddlSqlLowerCase, "`", "` ");
            } else if (ddlSqlLowerCase.indexOf("alter table") > -1) {
                if (ddlSqlLowerCase.indexOf("`.") > -1) {
                    tableName = getTableNameBySql(ddlSqlLowerCase, ".`", "`");
                } else {
                    tableName = getTableNameBySql(ddlSqlLowerCase, "alter table ", " ");
                }
            } else if (ddlSqlLowerCase.indexOf("create table if not exists ") > -1) {
                if (ddlSqlLowerCase.indexOf(".`") > -1) {
                    tableName = getTableNameBySql(ddlSqlLowerCase, ".`", "` ");
                } else {
                    tableName = getTableNameBySql(ddlSqlLowerCase, "create table if not exists ", "\n");
                }
            } else if (ddlSqlLowerCase.indexOf("create table") > -1) {
                if (ddlSqlLowerCase.indexOf(".`") > -1) {
                    tableName = getTableNameBySql(ddlSqlLowerCase, ".`", "` ");
                } else {
                    tableName = getTableNameBySql(ddlSqlLowerCase, "`", "`");
                }
            } else if (ddlSqlLowerCase.indexOf("truncate table ") > -1) {
                if (ddlSqlLowerCase.indexOf(".`") > -1) {
                    tableName = getTableNameBySql(ddlSqlLowerCase, ".`", "` ");
                } else {
                    tableName = getTableNameBySql(ddlSqlLowerCase, "truncate table ", null);
                }
            } else {
                tableName = getTableNameBySql(ddlSqlLowerCase, "`", "`");
            }
        } catch (Exception e) {
            LOG.error("解析DDLSQL失败:" + ddlSql, e);
            return null;
        }

        if (tableName.indexOf("`") > -1) {
            tableName = getTableNameBySql(tableName, "`", "`");
        }

        return tableName.replaceAll("`", "");
    }

    /**
     * 根据SQL截取表名
     *
     * @param sql   操作SQL
     * @param start 开始占位符
     * @param end   结束占位符
     * @return
     */
    private static String getTableNameBySql(String sql, String start, String end) {
        int startIndex = sql.indexOf(start) + start.length();
        int endIndex = sql.length();
        if (end != null) {
            endIndex = sql.indexOf(end, startIndex);
        }
        String tableName = sql.substring(startIndex, endIndex);
        return tableName;
    }


}
