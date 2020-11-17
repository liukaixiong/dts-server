package com.elab.data.dts.sender.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Module 发送器
 * @Description DDL类型的SQL处理
 * @Author liukaixiong
 * @Date 2020/11/16 19:55
 */
@Component
public class DDLKafkaSendProducer extends AbstractKafkaSender {

    @Value("${kafka.topic.ddl:}")
    private String topic;

    @Override
    protected String topic() {
        return topic;
    }
}
