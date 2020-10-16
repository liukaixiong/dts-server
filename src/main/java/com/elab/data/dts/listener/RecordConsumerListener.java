package com.elab.data.dts.listener;

import com.elab.data.dts.common.RecordListener;
import com.elab.data.dts.common.UserRecord;
import com.elab.data.dts.formats.avro.Record;
import com.elab.data.dts.listener.event.AbstractEventProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消费dts的数据监听器
 *
 * @author ： liukx
 * @time ： 2020/9/22 - 17:22
 */
@Component
public class RecordConsumerListener implements RecordListener {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private List<AbstractEventProcess> eventProcessList;

    @Override
    public void consume(UserRecord userRecord) {
        try {
            Record record = userRecord.getRecord();
            eventProcessList.forEach((V) -> {
                if (V.subscription(record.getOperation())) {
                    V.processEvent(userRecord);
                }
            });
            // 提交消费位点
            userRecord.commit(String.valueOf(record.getSourceTimestamp()));
        } catch (Exception e) {
            logger.error("消费失败", e);
        }
    }

}
