package com.elab.data.dts.sender;

import com.elab.data.dts.model.DDLData;
import com.elab.data.dts.model.TableData;

/**
 * @Module 监控
 * @Description 定义监控数据请求
 * @Date 2020/10/20 13:48
 * @Author liukaixiong
 */
public interface IMonitorDataProducer {

    /**
     * 异常数据发送
     *
     * @param e   异常信息
     * @param obj 相关的数据
     */
    public void sendError(Throwable e, Object obj);

    /**
     * 发送表情况数据
     *
     * @param tableData
     */
    public void sendTableInfo(TableData tableData);

    /**
     * 发送ddl语句
     *
     * @param ddlData
     */
    public void sendDDLInfo(DDLData ddlData);


}
