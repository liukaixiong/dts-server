package com.elab.data.dts.recordprocessor;

import com.elab.data.dts.formats.avro.Field;
import com.elab.data.dts.recordprocessor.mysql.MysqlFieldConverter;
import org.apache.commons.lang3.StringUtils;

public interface FieldConverter {
    FieldValue convert(Field field, Object o);
    public static FieldConverter getConverter(String sourceName, String sourceVersion) {
        if (StringUtils.endsWithIgnoreCase("mysql", sourceName)) {
            return new MysqlFieldConverter();
        } else {
            throw new RuntimeException("FieldConverter: only mysql supported for now");
        }
    }
}
