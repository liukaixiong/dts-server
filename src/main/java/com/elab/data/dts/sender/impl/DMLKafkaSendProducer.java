package com.elab.data.dts.sender.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * kafka推送消息
 *
 * @author ： liukx
 * @time ： 2020/10/16 - 14:08
 */
@Component
@Lazy
public class DMLKafkaSendProducer extends AbstractKafkaSender {

    private Logger logger = LoggerFactory.getLogger(DMLKafkaSendProducer.class);

    @Override
    protected String topic() {
        return kafkaProperties.getTemplate().getDefaultTopic();
    }
}
