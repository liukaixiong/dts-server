package com.elab.data.dts.listener.event;

import com.alibaba.fastjson.JSON;
import com.elab.data.dts.DtsServerApplication;
import com.elab.data.dts.model.DMLData;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DtsServerApplication.class})
public class DataEventProcessTest extends TestCase {
    @Autowired
    DataEventProcess dataEventProcess;
    @Test
    public void testProcess0() {
        String json = "{\"databaseName\":\"marketing_db\",\"sourceTimestamp\":1616055610,\"fieldDataMap\":{\"sign\":{\"field\":\"sign\",\"dataType\":\"java.lang.Integer\",\"oldValue\":\"0\",\"value\":\"0\"},\"real_name\":{\"field\":\"real_name\",\"dataType\":\"java.lang.String\"},\"source\":{\"field\":\"source\",\"dataType\":\"java.lang.Integer\",\"oldValue\":\"2\",\"value\":\"2\"},\"score\":{\"field\":\"score\",\"dataType\":\"java.lang.Integer\"},\"hist_source\":{\"field\":\"hist_source\",\"dataType\":\"java.lang.Integer\"},\"id\":{\"field\":\"id\",\"dataType\":\"java.lang.Integer\",\"oldValue\":\"630723\",\"value\":\"630723\"},\"last_grade_time\":{\"field\":\"last_grade_time\",\"dataType\":\"java.util.Date\",\"oldValue\":\"2021-03-14 01:58:08\",\"value\":\"2021-03-14 01:58:08\"},\"visit_name\":{\"field\":\"visit_name\",\"dataType\":\"java.lang.String\"},\"house_id\":{\"field\":\"house_id\",\"dataType\":\"java.lang.Integer\",\"oldValue\":\"10411\",\"value\":\"10411\"},\"creator\":{\"field\":\"creator\",\"dataType\":\"java.lang.String\"},\"validate_name\":{\"field\":\"validate_name\",\"dataType\":\"java.lang.String\"},\"level\":{\"field\":\"level\",\"dataType\":\"java.lang.Integer\"},\"created\":{\"field\":\"created\",\"dataType\":\"java.util.Date\",\"oldValue\":\"2020-03-18 16:24:34\",\"value\":\"2020-03-18 16:24:34\"},\"serial_number\":{\"field\":\"serial_number\",\"dataType\":\"java.lang.String\",\"oldValue\":\"86578af17e8fb9775566861cd6e4fe30\",\"value\":\"86578af17e8fb9775566861cd6e4fe30\"},\"brand_id\":{\"field\":\"brand_id\",\"dataType\":\"java.lang.Integer\",\"oldValue\":\"13\",\"value\":\"13\"},\"dispatch_order_status\":{\"field\":\"dispatch_order_status\",\"dataType\":\"java.lang.Integer\",\"oldValue\":\"1\",\"value\":\"1\"},\"hist_mobile_time\":{\"field\":\"hist_mobile_time\",\"dataType\":\"java.util.Date\"},\"user_id\":{\"field\":\"user_id\",\"dataType\":\"java.lang.Integer\",\"oldValue\":\"397726\",\"value\":\"397726\"},\"grade\":{\"field\":\"grade\",\"dataType\":\"java.lang.Integer\",\"oldValue\":\"3\",\"value\":\"3\"},\"updator\":{\"field\":\"updator\",\"dataType\":\"java.lang.String\",\"oldValue\":\"system\",\"value\":\"system\"},\"other_name\":{\"field\":\"other_name\",\"dataType\":\"java.lang.String\"},\"mobile_time\":{\"field\":\"mobile_time\",\"dataType\":\"java.util.Date\",\"oldValue\":\"2020-03-18 16:24:34\",\"value\":\"2020-03-18 16:24:34\"},\"report_name\":{\"field\":\"report_name\",\"dataType\":\"java.lang.String\",\"oldValue\":\"王奎\",\"value\":\"王奎\"},\"updated\":{\"field\":\"updated\",\"dataType\":\"java.util.Date\",\"oldValue\":\"2021-03-18 16:15:08\",\"value\":\"2021-03-18 16:20:10\"},\"status\":{\"field\":\"status\",\"dataType\":\"java.lang.Integer\",\"oldValue\":\"1\",\"value\":\"1\"}},\"_catRootMessageId\":\"dts-server-ac13bdab-448904-855722\",\"changeFieldList\":[\"updated\"],\"id\":\"630723\",\"_catParentMessageId\":\"dts-server-ac13bdab-448904-855722\",\"_catChildMessageId\":\"MQ-CONSUMER-ac13bdab-448904-570474\",\"operation\":\"UPDATE\",\"tableName\":\"c_user_info\",\"application.name\":\"dts-server\"}";

        DMLData tableData = JSON.parseObject(json, DMLData.class);
        boolean b = dataEventProcess.filterData(tableData);
        System.out.println(b);
    }
}