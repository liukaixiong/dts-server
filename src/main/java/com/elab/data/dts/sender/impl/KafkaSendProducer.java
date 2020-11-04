package com.elab.data.dts.sender.impl;

import com.alibaba.fastjson.JSON;
import com.elab.data.dts.model.TableData;
import com.elab.data.dts.sender.ISendProducer;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Map;

/**
 * kafka推送消息
 *
 * @author ： liukx
 * @time ： 2020/10/16 - 14:08
 */
@Component
@Lazy
public class KafkaSendProducer implements ISendProducer {

    private Logger logger = LoggerFactory.getLogger(KafkaSendProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaProperties kafkaProperties;

    private Integer defaultPartition = 12;

    @Override
    public void send(TableData tableData) throws Exception {
        Map<String, String> properties = kafkaProperties.getProperties();
        String defaultTopic = kafkaProperties.getTemplate().getDefaultTopic();
        String partition = properties.get("partition");
        Integer partitionCount = defaultPartition;
        if (StringUtils.isNotEmpty(partition)) {
            partitionCount = Integer.valueOf(partition);
        }
        // 如果某个分区发生变化的话，由于一开始设定的分区数量不匹配可能就出现问题，暂时不考虑
        // List<PartitionInfo> partitionInfos = kafkaProducer.partitionsFor(defaultTopic);

        // 尽可能的希望同一张表的数据可以落到一个分区,确保顺序性
        int partitionIndex = tableData.getTableName().hashCode() & (partitionCount - 1);

        // 唯一的key,由于阿里云的kafka没有提供key的查询,所以暂时也不考虑.

        ProducerRecord<String, String> kafkaMessage = new ProducerRecord<String, String>(defaultTopic,
                partitionIndex, System.currentTimeMillis(), null, JSON.toJSONString(tableData));
        try {
            ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(kafkaMessage);
            SendResult<String, String> sendResult = listenableFuture.get();
            RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            // 默认不报错,应该就是发送成功了
            listenableFuture.completable();
            logger.debug("推送kafka成功..");
        } catch (Exception e) {
            logger.error("消息发送失败", e);
        }
    }

}
