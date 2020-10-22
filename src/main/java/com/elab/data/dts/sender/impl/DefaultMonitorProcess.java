package com.elab.data.dts.sender.impl;

import com.elab.data.dts.formats.avro.Operation;
import com.elab.data.dts.model.DDLData;
import com.elab.data.dts.model.TableData;
import com.elab.data.dts.sender.IMonitorDataProducer;
import org.springframework.stereotype.Component;

/**
 * @Module 监控
 * @Description 默认的监控触发实现
 * @Author liukaixiong
 * @Date 2020/10/20 17:09
 */
@Component
public class DefaultMonitorProcess implements IMonitorDataProducer {

    @Override
    public void sendError(Throwable e, Object obj) {
        // 消息处理失败的情况
    }

    @Override
    public void sendTableInfo(TableData tableData) {
        String tableName = tableData.getTableName();
        String databaseName = tableData.getDatabaseName();
        Operation operation = tableData.getOperation();
        // 每个表触发总数

        // 每个表的处理事件

        // 每个表的小时处理峰值

    }

    @Override
    public void sendDDLInfo(DDLData ddlData) {

    }
}
