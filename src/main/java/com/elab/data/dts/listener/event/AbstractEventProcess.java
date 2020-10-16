package com.elab.data.dts.listener.event;

import com.elab.data.dts.common.UserRecord;
import com.elab.data.dts.formats.avro.Operation;
import com.elab.data.dts.model.TableData;
import com.elab.data.dts.sender.ISendProducer;

/**
 * 事件执行器
 *
 * @author ： liukx
 * @time ： 2020/9/23 - 14:16
 */
public abstract class AbstractEventProcess {

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
     * 具体的业务处理器
     *
     * @param tableData 表的数据
     */
    protected void process(TableData tableData) {
        // 子类去实现,处理数据的逻辑
    }

    /**
     * 解析数据
     *
     * @param record
     * @return
     */
    protected abstract TableData parseTable(UserRecord record);

    public void processEvent(UserRecord record) {
        // 1. 解析数据
        TableData tableData = parseTable(record);

        // 2. 处理数据
        process(tableData);

        // 3. 发送数据
        sendData(tableData);
    }

    /**
     * 如果子类指定实现了getProducer方法,那么通过该方法进行发送，如果还有其他操作可以重写
     *
     * @param tableData
     */
    protected void sendData(TableData tableData) {
        ISendProducer producer = getProducer();
        if (producer != null) {
            producer.send(tableData);
        }
    }

}
