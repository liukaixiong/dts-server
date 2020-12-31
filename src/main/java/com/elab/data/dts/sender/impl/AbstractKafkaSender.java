package com.elab.data.dts.sender.impl;

import com.alibaba.fastjson.JSON;
import com.elab.data.dts.config.props.DTSProperties;
import com.elab.data.dts.model.DMLData;
import com.elab.data.dts.model.TableData;
import com.elab.data.dts.sender.ISendProducer;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Map;

/**
 * @Module 抽象类
 * @Description kafka发送统一管理
 * @Author liukaixiong
 * @Date 2020/11/16 19:51
 */
public abstract class AbstractKafkaSender implements ISendProducer {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    protected DTSProperties dtsProperties;

    @Autowired
    protected KafkaProperties kafkaProperties;

//    private Integer defaultPartition = 12;

    protected abstract String topic();

    @Override
    public void send(TableData tableData) {
//        Map<String, String> properties = kafkaProperties.getProperties();

//        String partition = properties.get("partition");
//        Integer partitionCount = defaultPartition;
//        if (StringUtils.isNotEmpty(partition)) {
//            partitionCount = Integer.valueOf(partition);
//        }

        // 如果某个分区发生变化的话，由于一开始设定的分区数量不匹配可能就出现问题，暂时不考虑
        // List<PartitionInfo> partitionInfos = kafkaProducer.partitionsFor(defaultTopic);

        // 尽可能的希望同一张表的数据可以落到一个分区,确保顺序性
        // 20201112 改为表加主键,因为某一张表产生改动特别大的情况下,那一个分区的消息会堆积的特别多
        // 改为采用key的方式,让kafka自己选择对应的分区 采用murmur2算法做hash
//        Integer partitionIndex = null;
//        if (tableData instanceof DMLData) {
//            DMLData dmlData = (DMLData) tableData;
//            String id = dmlData.getId();
//            partitionIndex = getPartitionIndex(tableData.getTableName(), id, partitionCount);
//        }

        String defaultTopic = topic();
        String key = null;
        Integer partition = null;
        if (tableData instanceof DMLData) {
            DMLData dmlData = (DMLData) tableData;
            Map<String, Integer> tablePartitionMap = dtsProperties.getTablePartitionMap();
            partition = tablePartitionMap.get(dmlData.getTableName());
            String id = dmlData.getId() == null ? "" : dmlData.getId();
            key = tableData.getTableName() + id;
        }

        ProducerRecord<String, String> kafkaMessage = new ProducerRecord<String, String>(defaultTopic,
                partition, System.currentTimeMillis(), key, JSON.toJSONString(tableData));
        try {
            ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(kafkaMessage);
            SendResult<String, String> sendResult = listenableFuture.get();
            RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            // 默认不报错,应该就是发送成功了
            listenableFuture.completable();
            String date = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(tableData.getSourceTimestamp() * 1000);
            logger.debug("推送kafka成功..[" + defaultTopic + "] 数据产生的来源时间:[" + date + "]");
        } catch (Exception e) {
            logger.error("消息发送失败", e);
        }
    }

    protected Integer getPartitionIndex(String tableName, String id, Integer partitionCount) {
        if (id == null || tableName == null || partitionCount == null) {
            return null;
        }
        return (tableName + "_" + id).hashCode() & (partitionCount - 1);
    }
}
