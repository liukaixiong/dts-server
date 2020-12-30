package com.elab.data.dts.sender;

import com.elab.data.dts.model.TableData;

/**
 * 发送生产者
 *
 * @author ： liukx
 * @time ： 2020/9/23 - 15:14
 */
public interface ISendProducer {
    /**
     * 发送数据
     *
     * @param tableData
     * @throws Exception
     */
    public void send(TableData tableData);

}
