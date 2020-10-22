package com.elab.data.dts.listener.event;

import com.elab.data.dts.common.UserRecord;
import com.elab.data.dts.formats.avro.Operation;
import com.elab.data.dts.model.TableData;
import com.elab.data.dts.sender.IMonitorDataProducer;
import com.elab.data.dts.sender.ISendProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 事件执行器
 *
 * @author ： liukx
 * @time ： 2020/9/23 - 14:16
 */
public abstract class AbstractEventProcess {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private IMonitorDataProducer producer;

    /**
     * 如果子类有实现代表希望通过发射器将收到的数据发送出去
     *
     * @return
     */
    public ISendProducer getProducer() {
        return null;
    }

    /**
     * 是否关注该操作名称
     *
     * @return
     */
    public abstract boolean subscription(Operation operation);

    /**
     * 处理表的业务数据,具体参考@Link processEvent
     * @param tableData
     * @return true 为后续的消息发送 false 表示后续消息不发送
     * @throws Exception
     */
    protected boolean process(TableData tableData) throws Exception {
        // 子类去实现,处理数据的逻辑
        return true;
    }

    /**
     * 解析数据
     *
     * @param record
     * @return
     */
    protected abstract TableData parseTable(UserRecord record);

    public void processEvent(UserRecord record) throws Exception {
        TableData tableData = null;
        try {
            // 1. 解析数据
            tableData = parseTable(record);

            // 2. 处理数据
            boolean process = process(tableData);

            // 3. 发送数据
            if (process) {
                sendData(tableData);
            }
        } catch (Exception e) {
            sendErrorMsg(e, record, tableData);
            log.error("处理数据失败", e);
        }
    }

    private void sendErrorMsg(Throwable e, Object o, TableData tableData) {
        if (producer != null) {

            if (tableData != null) {
                o = tableData;
            }

            producer.sendError(e, o);
        }
    }

    /**
     * 如果子类指定实现了getProducer方法,那么通过该方法进行发送，如果还有其他操作可以重写
     *
     * @param tableData
     */
    protected void sendData(TableData tableData) throws Exception {
        ISendProducer producer = getProducer();
        if (producer != null) {
            producer.send(tableData);
        }
    }

}
