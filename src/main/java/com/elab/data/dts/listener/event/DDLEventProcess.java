package com.elab.data.dts.listener.event;

import com.alibaba.fastjson.JSON;
import com.elab.data.dts.common.UserRecord;
import com.elab.data.dts.formats.avro.Operation;
import com.elab.data.dts.model.TableData;
import com.elab.data.dts.sender.ISendProducer;
import com.elab.data.dts.sender.impl.DDLKafkaSendProducer;
import com.elab.data.dts.utils.DataParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 修改事件触发
 *
 * @author ： liukx
 * @time ： 2020/9/23 - 14:15
 */
@Component
public class DDLEventProcess extends AbstractEventProcess {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private DDLKafkaSendProducer kafkaSendProducer;

    @Override
    public ISendProducer getProducer() {
        return kafkaSendProducer;
    }

    @Override
    public boolean subscription(Operation operation) {
        return Operation.DDL.equals(operation);
    }

    @Override
    protected boolean process0(TableData tableData) throws Exception {
        logger.debug("DDL数据处理:" + JSON.toJSONString(tableData));
        return true;
    }

    @Override
    protected TableData parseTable(UserRecord record) {
        return DataParseUtils.parseDDL(record.getRecord());
    }

}
