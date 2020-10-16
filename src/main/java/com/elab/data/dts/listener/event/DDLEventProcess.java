package com.elab.data.dts.listener.event;

import com.elab.data.dts.common.UserRecord;
import com.elab.data.dts.formats.avro.Operation;
import com.elab.data.dts.model.TableData;
import com.elab.data.dts.utils.DataParseUtils;
import org.springframework.stereotype.Component;

/**
 * 修改事件触发
 *
 * @author ： liukx
 * @time ： 2020/9/23 - 14:15
 */
@Component
public class DDLEventProcess extends AbstractEventProcess {

    @Override
    public boolean subscription(Operation operation) {
        return Operation.DDL.equals(operation);
    }

    @Override
    protected TableData parseTable(UserRecord record) {
        return DataParseUtils.parseDDL(record.getRecord());
    }
}
