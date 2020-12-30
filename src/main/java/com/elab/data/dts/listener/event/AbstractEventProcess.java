package com.elab.data.dts.listener.event;

import com.elab.data.dts.common.UserRecord;
import com.elab.data.dts.components.DebugValueComponent;
import com.elab.data.dts.config.props.DTSProperties;
import com.elab.data.dts.consts.DTSConstants;
import com.elab.data.dts.formats.avro.Operation;
import com.elab.data.dts.model.DMLData;
import com.elab.data.dts.model.TableData;
import com.elab.data.dts.sender.IMonitorDataProducer;
import com.elab.data.dts.sender.ISendProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 事件执行器
 *
 * @author ： liukx
 * @time ： 2020/9/23 - 14:16
 */
public abstract class AbstractEventProcess {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private IMonitorDataProducer producer;

    @Autowired
    protected DTSProperties dtsProperties;

    @Autowired
    private DebugValueComponent debugValueComponent;

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
     * 处理表的业务数据,具体参考@Link AbstractEventProcess.processEvent()
     *
     * @param tableData
     * @return true 为后续的消息发送 false 表示后续消息不发送
     * @throws Exception
     */
    protected boolean process(TableData tableData) throws Exception {
        // 实现数据过滤

        if (filterData(tableData)) {
            return false;
        }

        return process0(tableData);
    }

    protected boolean filterData(TableData tableData) {

        if (debugValueComponent.isExcludeTableName(tableData.getTableName())) {
            logger.debug("该数据[" + tableData.getTableName() + "]属于debugValueComponent排除的数据，在DebugValueComponent中excludeDataInfo配置,application.yml配置中填写");
            return true;
        }

        // 先过滤排除的
        Map<String, List<String>> excludeDataInfo = dtsProperties.getExcludeDataInfo();
        if (isSubscriptionData(tableData, excludeDataInfo)) {
            logger.debug("该数据[" + tableData.getTableName() + "]属于需要排除的数据，在dtsProperties中excludeDataInfo配置,application.yml配置中填写");
            return true;
        }

        // 然后再过滤不关注的
        Map<String, List<String>> includeDataInfo = dtsProperties.getIncludeDataInfo();
        if (!isSubscriptionData(tableData, includeDataInfo)) {
            logger.debug("该数据属[" + tableData.getTableName() + "]于非关注数据，在dtsProperties中includeDataInfo中定义,application.yml配置中填写");
            return true;
        }

        // 过滤掉因为特殊原因产生的改变字段值
        Map<String, List<String>> excludeTableChangeField = dtsProperties.getExcludeTableChangeField();
        if (isChangeFieldData(tableData, excludeTableChangeField)) {
            logger.debug("该数据属[" + tableData.getTableName() + "]于非关注数据，在dtsProperties中excludeTableChangeField中定义,application.yml配置中填写");
            return true;
        }

        return false;
    }

    /**
     * 业务回调处理
     *
     * @param tableData
     * @return
     * @throws Exception
     */
    protected abstract boolean process0(TableData tableData) throws Exception;

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
            logger.error("处理数据失败", e);
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

    /**
     * 是否关注个该数据
     *
     * @param tableData
     * @param ruleDataInfo
     * @return
     */
    protected boolean isSubscriptionData(TableData tableData, Map<String, List<String>> ruleDataInfo) {
        String databaseName = tableData.getDatabaseName();
        String tableName = tableData.getTableName();
        if (ruleDataInfo != null) {
            List<String> tables = ruleDataInfo.get(databaseName);
            if (tables != null && (tables.contains(DTSConstants.MATCH_ALL) || tables.contains(tableName))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否匹配表的改变字段
     *
     * @param tableData
     * @param ruleDataInfo
     * @return
     */
    protected boolean isChangeFieldData(TableData tableData, Map<String, List<String>> ruleDataInfo) {
        String tableName = tableData.getTableName();
        if (ruleDataInfo != null && tableData.getOperation().equals(Operation.UPDATE) && tableData instanceof DMLData) {
            // 过滤所有表的这些改变字段
            List<String> allFieldList = ruleDataInfo.get(DTSConstants.MATCH_ALL);
            if (allFieldList != null) {
                List<String> changeFieldList = ((DMLData) tableData).getChangeFieldList();
                if (allFieldList.contains(DTSConstants.MATCH_ALL) || allFieldList.containsAll(changeFieldList)) {
                    return true;
                }
            }

            // 过滤掉指定表的特定字段
            List<String> fieldList = ruleDataInfo.get(tableName);
            if (fieldList != null) {
                List<String> changeFieldList = ((DMLData) tableData).getChangeFieldList();
                if (fieldList.contains(DTSConstants.MATCH_ALL) || fieldList.containsAll(changeFieldList)) {
                    return true;
                }
            }
        }
        return false;
    }
}
