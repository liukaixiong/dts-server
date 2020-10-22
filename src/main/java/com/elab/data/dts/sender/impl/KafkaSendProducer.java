package com.elab.data.dts.sender.impl;

import com.alibaba.fastjson.JSON;
import com.elab.data.dts.model.TableData;
import com.elab.data.dts.sender.ISendProducer;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.internals.ErrorLoggingCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * kafka推送消息
 *
 * @author ： liukx
 * @time ： 2020/10/16 - 14:08
 */
@Component
public class KafkaSendProducer implements ISendProducer {

    private Logger LOG = LoggerFactory.getLogger(KafkaSendProducer.class);

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Override
    public void send(TableData tableData) throws Exception {
        Map<String, String> properties = kafkaProperties.getProperties();
        String defaultTopic = kafkaProperties.getTemplate().getDefaultTopic();
        String partition = properties.get("partition");
        Integer partitionCount = null;
        if (StringUtils.isNotEmpty(partition)) {
            partitionCount = Integer.valueOf(partition);
        }
        // 如果某个分区发生变化的话，由于一开始设定的分区数量不匹配可能就出现问题，暂时不考虑
        // List<PartitionInfo> partitionInfos = kafkaProducer.partitionsFor(defaultTopic);

        // 尽可能的希望同一张表的数据可以落到一个分区,确保顺序性
        int partitionIndex = tableData.getTableName().hashCode() & (partitionCount - 1);

        // 唯一的key,由于阿里云的kafka没有提供key的查询,所以暂时也不考虑.

        String bodyValue = JSON.toJSONString(tableData);

        ProducerRecord<String, String> kafkaMessage = new ProducerRecord<String, String>(defaultTopic,
                partitionIndex, System.currentTimeMillis(), null, JSON
                .toJSONString(tableData));

        ErrorLoggingCallback errorLoggingCallback = new ErrorLoggingCallback(defaultTopic, null, bodyValue.getBytes(), true);

        try {
            Future<RecordMetadata> metadataFuture = kafkaProducer.send(kafkaMessage, errorLoggingCallback);
            RecordMetadata recordMetadata = metadataFuture.get();
            // 默认不报错,应该就是发送成功了
        } catch (Exception e) {
            e.printStackTrace();
            // 如果发送失败，尽可能的希望数据能够保存下来。
            LOG.error("推送kafka异常", e);
        }

    }

}
