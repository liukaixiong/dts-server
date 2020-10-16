package com.elab.data.dts.listener.event;

import com.alibaba.fastjson.JSON;
import com.elab.data.dts.common.UserRecord;
import com.elab.data.dts.formats.avro.Operation;
import com.elab.data.dts.model.TableData;
import com.elab.data.dts.sender.ISendProducer;
import com.elab.data.dts.sender.impl.KafkaSendProducer;
import com.elab.data.dts.utils.DataParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 修改事件触发
 *
 * @author ： liukx
 * @time ： 2020/9/23 - 14:15
 */
@Component
public class DataEventProcess extends AbstractEventProcess {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<Operation> subscriptionList = Arrays.asList(Operation.INSERT, Operation.UPDATE, Operation.DELETE);

    @Autowired
    private KafkaSendProducer sendProducer;

    @Override
    public boolean subscription(Operation operation) {
        return subscriptionList.contains(operation);
    }


    @Override
    protected TableData parseTable(UserRecord record) {
        return DataParseUtils.parseDML(record.getRecord());
    }

    @Override
    public ISendProducer getProducer() {
        return sendProducer;
    }

    @Override
    protected void process(TableData tableData) {
        logger.info(" 得到的转换数据 : " + JSON.toJSONString(tableData));
    }
}
